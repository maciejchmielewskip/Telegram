package org.telegram.ui.profileScreen;

public class ProfileViewModel {
    final String title;
    final String subtitle;
    final int centerColor;
    final int middleColor;
    final int edgeColor;


    public ProfileViewModel(String title, String subtitle, int centerColor, int middleColor, int edgeColor) {
        this.title = title;
        this.subtitle = subtitle;
        this.centerColor = centerColor;
        this.middleColor = middleColor;
        this.edgeColor = edgeColor;
    }
}
