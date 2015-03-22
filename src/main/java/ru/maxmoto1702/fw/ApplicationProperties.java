package ru.maxmoto1702.fw;

/**
 * Created by m on 22.03.2015.
 */
public class ApplicationProperties {
    private static String NAME = "Test Task";
    private static String VERSION = "1.0";
    private static String CLIENT_ID = "SRP5CUGB7ICSDHSGD74KAMV84HKLD934B401MBAPHKFCDU7P4UI6LSJBAIVPF5TP";
    private static String CLIENT_SECRET = "KOAP32GGM1VP00ICNAHMRSH0FEHDIQAIP3U4AVIFD9920DJN6R5UIRMJS4AF2I1C";

    public String getName() {
        return NAME;
    }

    public String getVersion() {
        return VERSION;
    }

    public String getClientId() {
        return CLIENT_ID;
    }

    public String getClientSecret() {
        return CLIENT_SECRET;
    }
}
