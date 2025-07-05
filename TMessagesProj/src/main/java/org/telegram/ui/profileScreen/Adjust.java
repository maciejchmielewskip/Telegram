package org.telegram.ui.profileScreen;


public class Adjust {
    public static class Header {
        public static int height = 805;
        public static int topMargin = 200;
        public static float edgeBallSize = 100;
        public static float particlesSpread = 300;
        public static float verticalCenterShift = 0.64f;
        public static float expandedRatio = 1.1712962963f;
        public static int buttonsBottom = 195;
        public static float regularTextsTop = 392 + Adjust.Header.topMargin;
        public static float expandedTextsBottom = 405;
    }

    public static class Gift {
        public static int size = 100;
    }

    public static class Avatar {
        public static int size = 240;
        public static float blurFadeSpeed = 5;
        public static float fadeScale = -0.7f;
        public static int blurStrength = 20;
        public static TimingFunction darken = new TimingFunction.Compound(
                new TimingFunction.Delay(0.13f),
                new TimingFunction.SpeedUp(3)
        );
    }

    public static class Stamp {
        public static int size = 50;
    }
}