package org.telegram.ui.profileScreen;

class HeaderGeometry {
    CartesianCoordinates size;
    Float scrollDownOffset;
    Float scrollDownProgress;
    Float extendedScrollDownProgress;
    Float scrollUpOffset;
    Float scrollUpProgress;

    public void updateScrollOffset(int scrollY) {
        scrollDownOffset = (float)scrollY;
        extendedScrollDownProgress = (float)scrollY / (float)Adjust.Header.height;
        scrollDownProgress = Math.max(0, extendedScrollDownProgress);
        scrollUpOffset = -(float)scrollY;
        scrollUpProgress = -(float)scrollY / (float)Adjust.Header.topMargin;
    }
}
