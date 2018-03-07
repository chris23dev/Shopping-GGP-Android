package com.ggp.theclub.manager.impl;

import android.app.Activity;
import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.event.FavoritesUpdateEvent;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.BriteVerifyManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.model.GigyaEmailResponse;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.GigyaUtils;
import com.ggp.theclub.util.StringUtils;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.android.GSAPI;

import java.util.HashSet;

import de.greenrobot.event.EventBus;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

public class AccountManagerImpl implements AccountManager {
    private static final String LOG_TAG = AccountManagerImpl.class.getSimpleName();
    private static final String CONFLICTING_ACCOUNT_MARKER = "conflictingAccount";
    private static final int SUCCESS_CODE = 0;
    private static final int REGISTRATION_REQUIRED_CODE = 206001;
    private static final int INVALID_CREDENTIALS_CODE = 403042;
    private static final int UNAUTHORIZED_USER_CODE = 403005;
    private static final int OLD_PASSWORD_USED_CODE = 401030;
    private static final int EMAIL_ADDRESS_NOT_FOUND_CODE = 403047;
    private static final String PROVIDER_OPTION = "provider";
    private static final String REG_TOKEN_OPTION = "regToken";
    private static final String PROFILE_OPTION = "profile";
    private static final String LOGIN_EMAIL_ADD_OPTION = "addLoginEmails";
    private static final String LOGIN_EMAIL_REMOVE_OPTION = "removeLoginEmails";
    private static final String DATA_OPTION = "data";
    private static final String EMAIL_OPTION = "email";
    private static final String PASSWORD_OPTION = "password";
    private static final String NEW_PASSWORD_OPTION = "newPassword";
    private static final String LOGIN_ID_OPTION = "loginID";
    private static final String METHOD_SET_ACCOUNT = "accounts.setAccountInfo";
    private static final String METHOD_GET_ACCOUNT = "accounts.getAccountInfo";
    private static final String METHOD_INITIALIZE_REGISTRATION = "accounts.initRegistration";
    private static final String METHOD_REGISTER = "accounts.register";
    private static final String METHOD_FINALIZE_REGISTRATION = "accounts.finalizeRegistration";
    private static final String METHOD_LOGIN_WITH_EMAIL = "accounts.login";
    private static final String METHOD_LOGOUT = "accounts.logout";
    private static final String METHOD_RESET_PASSWORD = "accounts.resetPassword";
    private static final String METHOD_CHECK_EMAIL = "accounts.isAvailableLoginID";
    private static final String METHOD_GET_CONFLICTING_ACCOUNTS= "accounts.getConflictingAccount";

    @Getter @Setter private User currentUser = new User();

    public AccountManagerImpl() {
        GSAPI.getInstance().initialize(MallApplication.getApp(), MallApplication.getApp().getString(R.string.gigya_key));
    }

    public boolean shouldHandlePermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return GSAPI.getInstance().handleAndroidPermissionsResult(requestCode, permissions, grantResults);
    }

    public void loginWithProvider(String provider, Activity activity, AccountListener accountListener) {
        GSObject options = new GSObject();
        options.put(PROVIDER_OPTION, provider);
        options.put(GigyaUtils.KEY_REG_SOURCE, GigyaUtils.KEY_ANDROID);

        AccountListener listenerWrapper = new AccountListener() {
            @Override
            public void onRegistrationRequired(GSObject data) {
                User account = new User(data);
                setCurrentUser(account);
                accountListener.onRegistrationRequired(data);
            }

            @Override
            public void onSuccess(GSObject data) {
                fetchAccountInfo(() -> accountListener.onSuccess(data));
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                accountListener.onFailure(data, errorMessage);
            }
        };
        sendRequest(options, activity, listenerWrapper);
    }

    public void logout(GigyaResponseListener gigyaResponseListener) {
        GigyaResponseListener responseListener = new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                setCurrentUser(new User());
                EventBus.getDefault().post(new AccountLoginEvent(false));
                gigyaResponseListener.onSuccess(data);
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                forceLogout();
                gigyaResponseListener.onSuccess(data);
            }
        };
        sendRequest(METHOD_LOGOUT, null, responseListener);
    }

    private void forceLogout() {
        GSAPI.getInstance().setSession(null);
        setCurrentUser(new User());
    }

    /**
     * This method checks if the email is a duplicate in gigya. It also checks if the email is valid through Briteverify
     */
    public void checkEmailValid(String email, Activity activity, EmailAvailabilityListener emailAvailabilityListener) {
        GSObject options = new GSObject();
        options.put(LOGIN_ID_OPTION, email);
        sendRequest(METHOD_CHECK_EMAIL, options, new AccountManagerImpl.GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                GigyaEmailResponse response = new GigyaEmailResponse(data);
                if (response.getIsAvailable()) {
                    validateWithBriteVerify(email, activity, emailAvailabilityListener);
                } else {
                    emailAvailabilityListener.onEmailUnavailable();
                }
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                emailAvailabilityListener.onFailure();
            }
        });
    }

    private void validateWithBriteVerify(String email, Activity activity, EmailAvailabilityListener emailAvailabilityListener) {
        BriteVerifyManager.checkEmailValid(email, new BriteVerifyManager.ValidEmailListener() {
            @Override
            public void onEmailValid() {
                //These callbacks will fail if they aren't forced onto the UI thread.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        emailAvailabilityListener.onEmailAvailable();
                    }
                });
            }

            @Override
            public void onEmailInvalid() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        emailAvailabilityListener.onEmailInvalid();
                    }
                });
            }
        });
    }

    public void registerEmail(User user, String password, GigyaResponseListener responseListener) {
        GigyaResponseListener responseListenerWrapper = new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                User responseObject = new User(data);
                user.setRegToken(responseObject.getRegToken());
                gigyaRegisterEmail(user, password, responseListener);
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                responseListener.onFailure(data, errorMessage);
            }
        };

        sendRequest(METHOD_INITIALIZE_REGISTRATION, null, responseListenerWrapper);
    }

    private void gigyaRegisterEmail(User user, String password, GigyaResponseListener responseListener) {
        GSObject options = new GSObject();
        options.put(REG_TOKEN_OPTION, user.getRegToken());
        options.put(GigyaUtils.KEY_REG_SOURCE, GigyaUtils.KEY_ANDROID);
        options.put(EMAIL_OPTION, user.getEmail());
        options.put(PROFILE_OPTION, GigyaUtils.buildProfileRequestObject(user));
        options.put(DATA_OPTION, GigyaUtils.buildRegisterDataObject(user));
        options.put(PASSWORD_OPTION, password);

        AccountListener responseListenerWrapper = new AccountListener() {
            @Override
            public void onSuccess(GSObject data) {
                fetchAccountInfo(() -> responseListener.onSuccess(data));
            }

            @Override
            public void onRegistrationRequired(GSObject data) {
                User responseObject = new User(data);
                setCurrentUser(responseObject);
                finalizeRegistration(responseObject.getRegToken(), responseListener);
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                Log.e(LOG_TAG, errorMessage);
                responseListener.onFailure(data, errorMessage);
            }
        };

        sendRequest(METHOD_REGISTER, options, responseListenerWrapper);
    }

    public void loginWithEmail(String email, String password, AccountLoginListener accountLoginListener) {
        GSObject options = new GSObject();
        options.put(LOGIN_ID_OPTION, email);
        options.put(PASSWORD_OPTION, password);
        AccountLoginListener listenerWrapper = new AccountLoginListener() {
            @Override
            public void onInvalidCredentials() {
                accountLoginListener.onInvalidCredentials();
            }

            @Override
            public void onSuccess(GSObject data) {
                fetchAccountInfo(() -> accountLoginListener.onSuccess(data));
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                accountLoginListener.onFailure(data, errorMessage);
            }
        };
        sendRequest(METHOD_LOGIN_WITH_EMAIL, options, listenerWrapper);
    }
    
    public void resetPassword(String emailAddress, ResetPasswordListener resetPasswordListener) {
        GSObject options = new GSObject();
        options.put(LOGIN_ID_OPTION, emailAddress);
        sendRequest(METHOD_RESET_PASSWORD, options, resetPasswordListener);
    }

    public void register(User user, GigyaResponseListener gigyaResponseListener) {
        GSObject options = new GSObject();
        options.put(REG_TOKEN_OPTION, user.getRegToken());
        options.put(PROFILE_OPTION, GigyaUtils.buildProfileRequestObject(user));
        options.put(DATA_OPTION, GigyaUtils.buildRegisterDataObject(user));
        GigyaResponseListener responseListenerWrapper = new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                finalizeRegistration(user.getRegToken(), gigyaResponseListener);
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                gigyaResponseListener.onFailure(data, errorMessage);
            }
        };

        sendRequest(METHOD_SET_ACCOUNT, options, responseListenerWrapper);
    }

    public void savePassword(String currentPassword, String newPassword, AccountLoginListener accountLoginListener) {
        GSObject options = new GSObject();
        options.put(PASSWORD_OPTION, currentPassword);
        options.put(NEW_PASSWORD_OPTION, newPassword);
        sendRequest(METHOD_SET_ACCOUNT, options, accountLoginListener);
    }

    public void saveAccountInfo(User user, GigyaResponseListener gigyaResponseListener) {
        GSObject options = new GSObject();
        options.put(DATA_OPTION, GigyaUtils.buildSaveAccountObject(user));
        options.put(PROFILE_OPTION, GigyaUtils.buildProfileRequestObject(user));
        if(!StringUtils.isEmpty(user.getEmail())) {
            options.put(LOGIN_EMAIL_ADD_OPTION, user.getEmail());
            options.put(LOGIN_EMAIL_REMOVE_OPTION, getCurrentUser().getEmail());
        }
        sendRequest(METHOD_SET_ACCOUNT, options, gigyaResponseListener);
    }

    private void saveFavorites(HashSet<Integer> favoriteTenantsList) {
        GSObject options = new GSObject();
        options.put(DATA_OPTION, GigyaUtils.buildFavoriteTenantsObject(favoriteTenantsList));
        sendRequest(METHOD_SET_ACCOUNT, options);
        EventBus.getDefault().post(new FavoritesUpdateEvent());
    }

    public boolean isLoggedIn() {
        return GSAPI.getInstance().getSession() != null && GSAPI.getInstance().getSession().isValid() && getCurrentUser() != null;
    }

    private void saveUserId(GSObject data) {
        try {
            PreferencesManager.getInstance().setUserId(data.getString(GigyaUtils.KEY_UID));
        } catch (GSKeyNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void fetchAccountInfo(AccountInfoListener accountInfoListener) {
        GigyaResponseListener responseListener = new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                User user = new User(data);
                setCurrentUser(user);
                accountInfoListener.onReturn();
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                setCurrentUser(new User());
                accountInfoListener.onReturn();
            }
        };
        sendRequest(METHOD_GET_ACCOUNT, null, responseListener);
    }

    public Observable<User> fetchAccountInfo() {
        if (getCurrentUser().getLoginProvider() != null) {
            return Observable.just(getCurrentUser());
        }
        Observable accountFetchObservable = Observable.create(subscriber -> {
            fetchAccountInfo(new AccountInfoListener() {
                @Override
                public void onReturn() {
                    subscriber.onNext(getCurrentUser());
                }
            });
        });
        return accountFetchObservable;
    }

    private void finalizeRegistration(String regToken, GigyaResponseListener gigyaResponseListener) {
        GSObject options = new GSObject();
        options.put(REG_TOKEN_OPTION, regToken);

        GigyaResponseListener responseListener = new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                fetchAccountInfo(() -> gigyaResponseListener.onSuccess(data));
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                gigyaResponseListener.onFailure(data, errorMessage);
            }
        };

        sendRequest(METHOD_FINALIZE_REGISTRATION, options, responseListener);
    }

    public void checkConflictingAccounts(String regToken, EmailAvailabilityListener emailAvailabilityListener) {
        GSObject options = new GSObject();
        options.put(REG_TOKEN_OPTION, regToken);

        try {
            GSAPI.getInstance().sendRequest(METHOD_GET_CONFLICTING_ACCOUNTS, options, (method, response, context) -> {
                if (responseContainsConflictingAccount(response)) {
                    emailAvailabilityListener.onEmailUnavailable();
                } else {
                    emailAvailabilityListener.onEmailAvailable();
                }
            }, null);
        } catch (Exception ex) {
            emailAvailabilityListener.onFailure();
        }
    }

    /**
     * This is a hacky way to check if the response had a conflicting account in it, but we don't actually
     * care about the data right now so it suffices.
     */
    private boolean responseContainsConflictingAccount(GSResponse response) {
        return response.getData().toString().contains(CONFLICTING_ACCOUNT_MARKER);
    }

    private void sendRequest(String methodName, GSObject options) {
        sendRequest(methodName, options, new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {}

            @Override
            public void onFailure(GSObject data, String errorMessage) {}
        });
    }

    private void sendRequest(String methodName, GSObject options, GigyaResponseListener gigyaResponseListener) {
        try {
            GSAPI.getInstance().sendRequest(methodName, options, (method, response, context) -> {
                if (response.getErrorCode() == SUCCESS_CODE) {
                    saveUserId(response.getData());
                    gigyaResponseListener.onSuccess(response.getData());
                } else if(response.getErrorCode() == UNAUTHORIZED_USER_CODE) {
                    forceLogout();
                    gigyaResponseListener.onFailure(response.getData(), response.getErrorMessage());
                } else {
                    gigyaResponseListener.onFailure(response.getData(), response.getErrorMessage());
                }
            }, null);
        } catch (Exception ex) {
            gigyaResponseListener.onFailure(new GSObject(), ex.getMessage());
        }
    }

    private void sendRequest(String methodName, GSObject options, AccountListener accountListener) {
        try {
            GSAPI.getInstance().sendRequest(methodName, options, (method, response, context) -> {
                if (response.getErrorCode() == SUCCESS_CODE) {
                    saveUserId(response.getData());
                    accountListener.onSuccess(response.getData());
                } else if (response.getErrorCode() == REGISTRATION_REQUIRED_CODE) {
                    accountListener.onRegistrationRequired(response.getData());
                } else {
                    accountListener.onFailure(response.getData(), response.getErrorMessage());
                }
            }, null);
        } catch (Exception ex) {
            accountListener.onFailure(new GSObject(), ex.getMessage());
        }
    }

    private void sendRequest(GSObject options, Activity activity, AccountListener accountListener) {
        try {
            GSAPI.getInstance().login(activity, options, (method, response, context) -> {
                if (response.getErrorCode() == SUCCESS_CODE) {
                    saveUserId(response.getData());
                    accountListener.onSuccess(response.getData());
                } else if (response.getErrorCode() == REGISTRATION_REQUIRED_CODE) {
                    accountListener.onRegistrationRequired(response.getData());
                } else {
                    accountListener.onFailure(response.getData(), response.getErrorMessage());
                }
            }, null);
        } catch (Exception ex) {
            accountListener.onFailure(new GSObject(), ex.getMessage());
        }
    }

    private void sendRequest(String methodName, GSObject options, AccountLoginListener accountLoginListener) {
        try {
            GSAPI.getInstance().sendRequest(methodName, options, (method, response, context) -> {
                if (response.getErrorCode() == SUCCESS_CODE) {
                    accountLoginListener.onSuccess(response.getData());
                } else if (response.getErrorCode() == INVALID_CREDENTIALS_CODE || response.getErrorCode() == OLD_PASSWORD_USED_CODE) {
                    saveUserId(response.getData());
                    accountLoginListener.onInvalidCredentials();
                } else {
                    accountLoginListener.onFailure(response.getData(), response.getErrorMessage());
                }
            }, null);
        } catch (Exception ex) {
            accountLoginListener.onFailure(new GSObject(), ex.getMessage());
        }
    }

    private void sendRequest(String methodName, GSObject options, ResetPasswordListener resetPasswordListener) {
        try {
            GSAPI.getInstance().sendRequest(methodName, options, (method, response, context) -> {
                if (response.getErrorCode() == SUCCESS_CODE) {
                    resetPasswordListener.onSuccess(response.getData());
                } else if (response.getErrorCode() == EMAIL_ADDRESS_NOT_FOUND_CODE) {
                    resetPasswordListener.onEmailNotFound();
                } else {
                    resetPasswordListener.onFailure(response.getData(), response.getErrorMessage());
                }
            }, null);
        } catch (Exception ex) {
            resetPasswordListener.onFailure(new GSObject(), ex.getMessage());
        }
    }

    @Override
    public void addFavoriteTenant(Tenant tenant) {
        currentUser.addFavoriteTenant(tenant);
        saveFavorites(currentUser.getFavorites());
    }

    @Override
    public void removeFavoriteTenant(Tenant tenant) {
        currentUser.removeFavoriteTenant(tenant);
        saveFavorites(currentUser.getFavorites());
    }
}
