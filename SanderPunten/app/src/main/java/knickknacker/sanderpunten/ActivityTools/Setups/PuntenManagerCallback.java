package knickknacker.sanderpunten.ActivityTools.Setups;

import java.util.ArrayList;

import knickknacker.shared.Signables.PublicUserData;

public interface PuntenManagerCallback {
    void getUsers();

    void addedSanderPunten(ArrayList<PublicUserData> changedUsers);

    void addedSanderPuntenMe(long punten);

    boolean isAdmin();
}
