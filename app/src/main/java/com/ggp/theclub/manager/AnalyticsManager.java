package com.ggp.theclub.manager;

import android.app.Activity;

import java.util.HashMap;

public interface AnalyticsManager {

    // Track Lifecycle
    public void startTrackingLifecycleData(Activity activity);

    public void stopTrackingLifecycleData();

    // Track Screens
    public void trackScreen(String screen);

    public void trackScreen(String screen, String tenant);

    // Track Actions
    public void trackAction(String action);
    public void trackAction(String action, HashMap<String, Object> contextData);

    public void trackAction(String action, HashMap<String, Object> contextData, String tenant);

    public void safePut(String key, String value, HashMap<String, Object> contextData);

    // FragmentTags
    class Screens {
        public static String JustForYou = "just for you";
        public static String Featured = "featured"; //TODO confirm string
        public static String Directory = "directory list";
        public static String DirectoryMap = "directory map";
        public static String Sales = "shopping";
        public static String SalesDetail = "sale detail";
        public static String Events = "events"; //TODO: confirm string
        public static String Dining = "dining";
        public static String Movies = "movies";
        public static String More = "more";
        public static String TenantDetail = "tenant detail";
        public static String TenantDetailMap = "tenant map";
        public static String ParkingReminder = "parking reminder";
        public static String MallInfo = "mall info";
        public static String MallSelection = "mall selection";
        public static String Parking = "parking info";
        public static String ParkingMap = "parking map";
        public static String ParkingAvailability = "parking availability";
        public static String Feedback = "feedback";
        public static String TermsConditions = "terms and conditions";
        public static String BlackFridayHours = "black friday hours";
        public static String Privacy = "privacy";
        public static String EventDetails = "event details";
        public static String Wayfinding = "wayfinding";
        public static String Account = "account";
    }

    class Actions {
        public static String Alert = "appevent.globalalert";
        public static String TileTap = "appevent.tile.tap";
        public static String NavParkingReminder = "appevent.navigation.setremindertap";
        public static String NavTermsAndConditions = "appevent.navigation.terms";
        public static String NavMallHours = "appevent.navigation.mallhours";
        public static String NavPrivacy = "appevent.navigation.privacy";
        public static String NavCall = "appevent.navigation.taptocall";
        public static String NavDirections = "appevent.navigation.addresstap";
        public static String SocialBadge = "appevent.navigation.socialbadge";
        public static String SearchByLocation = "appevent.search.location";
        public static String SearchByMallName = "appevent.search.mallname";
        public static String SelectedMall = "appevent.search.mallselected";
        public static String DirectoryChangeView = "appevent.directory.changeview";
        public static String DirectoryFilterCategory = "appevent.directory.categorytap";
        public static String DirectoryRegister = "appevent.directory.register";
        public static String ParkingForStore = "appevent.parking.findcomplete";
        public static String ParkAssistSearch = "appevent.parking.findmycar";
        public static String ParkAssistLevels = "appevent.parking.availbilitybylevel";
        public static String ParkingReminderSave = "appevent.parking.savereminder";
        public static String ParkingReminderAddNote = "appevent.parking.addnote";
        public static String TenantCall = "appevent.tenant.phonetap";
        public static String TenantWebsite = "appevent.tenant.websitetap";
        public static String TenantHours = "appevent.tenant.hourstap";
        public static String TenantMap = "appevent.tenant.directorymaptap";
        public static String TenantWayfinding = "appevent.tenant.guidemetap";
        public static String TenantFavorite = "appevent.tenant.favoritetap";
        public static String TenantDetails = "appevent.tenant.detailstap";
        public static String TenantDirections = "appevent.tenant.getdirections";
        public static String TenantRegister = "appevent.tenant.register";
        public static String TenantSale = "appevent.tenant.eventsaletile";
        public static String BuyTickets = "appevent.fandango.buytickets";
        public static String ReserveOpenTable = "appevent.tenant.opentabletap";
        public static String EventViewLocation = "appevent.eventdetail.location";
        public static String RegisterUser = "appevent.registration.submit";
        public static String AuthenticateUser = "appevent.authentication.submit";
        public static String SavePreferences = "appevent.account.changesettings";
        public static String WayfindingNavigate = "appevent.wayfinding.navigate";
        public static String MyInfoSave = "appevent.account.changepersonalinfo";
        public static String ChangePassword = "appevent.account.changepassword";
        public static String GetParkingDirections = "appevent.parking.getdirections";
        public static String SocialShare = "appevent.social.share";
        public static String ChangeMall = "appevent.navigation.changemall";
        public static String ParkingAvailabilitySelect = "appevent.parking.availabilitymenu";
        public static String EventDetailLinkClick = "appevent.eventdetail.ctalink";
        public static String ShoppingCategory = "appevent.shopping.category";
        public static String ShoppingSubcategory = "appevent.shopping.subcategory";
        public static String ShoppingFilterByStore = "appevent.shopping.filter.store";
        public static String ShoppingFilterBySort = "appevent.shopping.filter.sort";
        public static String MultiTheaterMallTheater = "appevent.multitheatermall.theater";
        public static String StoreInStore = "appevent.tenant.storeinstore";
        public static String AmenityCategory = "appevent.amenities.category";
    }
    
    class ContextDataKeys {
        public static String ScreenName = "screen.name";
        public static String PreviousScreenName = "screen.previous";
        public static String MallName = "mall.name";
        public static String MallPhoneNumber = "mall.phonenumber";
        public static String TenantName = "tenant.name";
        public static String TileName = "tile.name";
        public static String TileType = "tile.type";
        public static String TilePosition = "tile.position";
        public static String TenantPhoneNumber = "tenant.phonenumber";
        public static String SearchMallKeyword = "search.keyword";
        public static String SearchMallCount = "search.total";
        public static String SelectedMallCoordinates = "mall.coordinates";
        public static String SelectedMallDistance = "mall.distance";
        public static String DirectoryViewType = "directory.viewtype";
        public static String DirectoryFilterCategory = "directory.categoryname";
        public static String DirectoryFilterBrand = "directory.brandname";
        public static String DirectoryFilterProductType = "directory.productname:";
        public static String ParkingStore = "findparking.storename";
        public static String BuyTicketsMovieName = "fandango.moviename";
        public static String BuyTicketsDaysInAdvance= "fandango.daysinadvance";
        public static String SocialNetwork = "social.network";
        public static String AuthStatus = "auth.status";
        public static String AuthType = "auth.type";
        public static String UserId = "user.id";
        public static String FormName = "form.name";
        public static String CustomerLeadType = "customer.leadtype";
        public static String CustomerLeadLevel = "customer.leadlevel";
        public static String PreferencesEmail = "account.emailoptin";
        public static String PreferencesSMS = "account.textoptin";
        public static String PreferencesSweepstakes = "account.sweepstakes";
        public static String WayfindingStart = "wayfinding.start";
        public static String WayfindingEnd = "wayfinding.end";
        public static String WayfindingStartLevel = "wayfinding.startlevel";
        public static String WayfindingEndLevel = "wayfinding.endlevel";
        public static String TenantFavorite = "tenant.favoritestatus";
        public static String EventSaleName = "eventsale.name";
        public static String EventLinkText = "ctalink.text";
        public static String EventLinkType = "ctalink.type";
        public static String ParkingDaysInAdvance = "parking.daysinadvance";
        public static String ParkingTimeOfDay = "parking.timeofday";
        public static String ShoppingCategoryName = "shopping.categoryname";
        public static String ShoppingSubcategoryName = "shopping.subcategoryname";
        public static String ShoppingFilterStore = "shopping.filterstore";
        public static String ShoppingFilterSort = "shopping.filtersort";
        public static String MultiTheaterMallTheater = "multitheatermall.theater";
        public static String AmenityCategoryName = "amenities.category";
    }

    class ContextDataValues {
        public static String DirectoryViewTypeList = "list";
        public static String DirectoryViewTypeMap = "map";
        public static String HomeViewTypeMallEvent = "event";
        public static String HomeViewTypeMallSales = "sale";
        public static String BrowseSales = "browse sales";
        public static String SocialNetworkFacebook = "facebook";
        public static String SocialNetworkTwitter = "twitter";
        public static String SocialNetworkPinterest = "pinterest";
        public static String SocialNetworkInstagram = "instagram";

        public static String FormName = "site registration";
        public static String CustomerLeadType = "site registration";
        public static String CustomerLeadLevel = "partial: email";

        public static String AuthStatusAuthenticated = "authenticated";
        public static String AuthStatusUnauthenticated = "not authenticated";
        public static String AuthTypeEmail = "form entry";

        public static String GuestUser = "guest";

        public static String TenantFavorite = "favorite";
        public static String TenantFavoriteUnfavorite = "unfavorite";

        public static String PrimaryLink = "primary";
        public static String SecondaryLink = "secondary";

        public static String AmenityCategoryATM = "atm";
        public static String AmenityCategoryRestrooms = "restrooms";
    }
}