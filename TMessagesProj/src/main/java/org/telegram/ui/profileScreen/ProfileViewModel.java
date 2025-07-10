package org.telegram.ui.profileScreen;

public class ProfileViewModel {
    final String title;
    final String subtitle;
    final int centerColor;
    final int middleColor;
    final int edgeColor;
    final int stampColor;
    final int backButtonDrawable;
    final int moreButtonDrawable;
    final int backgroundColor;
    final int giftsCount;


    public ProfileViewModel(String title, String subtitle, int centerColor, int middleColor, int edgeColor, int stampColor, int backButtonDrawable, int moreButtonDrawable, int backgroundColor, int giftsCount) {
        this.title = title;
        this.subtitle = subtitle;
        this.centerColor = centerColor;
        this.middleColor = middleColor;
        this.edgeColor = edgeColor;
        this.stampColor = stampColor;
        this.backButtonDrawable = backButtonDrawable;
        this.moreButtonDrawable = moreButtonDrawable;
        this.backgroundColor = backgroundColor;
        this.giftsCount = giftsCount;
    }
}
