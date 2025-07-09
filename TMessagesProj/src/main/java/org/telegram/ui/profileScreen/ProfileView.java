package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.FrameLayout;
import android.view.ViewTreeObserver;

public class ProfileView extends ScrollView {
    private PhysicsScroller scroller;
    private GradientBackgroundView backgroundView;
    private Avatar avatar;
    private AvatarMetaball avatarMetaball;
    public StampsController stampsController;
    public GiftsController giftsController;
    private HeaderGeometry headerGeometry = new HeaderGeometry();
    private MaskedView maskedView;
    private TextsView textsView;
    private ExpandingAvatar expandingAvatar;
    public FrameLayout rootFrame;
    private FrameLayout.LayoutParams buttonsLayout;
    private FrameLayout.LayoutParams textsLayout;
    private ButtonsLayout buttonsLayoutView;
    private LinearLayout contentColumn;
    private LayoutParams backLayoutParams;
    private LayoutParams moreLayoutParams;
    public FrameLayout mediaLayout;
    private FrameLayout headerFrame;

    public ProfileView(Context context, ProfileViewModel viewModel) {
        super(context);

        rootFrame = new FrameLayout(context);
        rootFrame.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rootFrame.setClipChildren(false);
        rootFrame.setClipToPadding(false);

        LinearLayout scrollableView = new LinearLayout(context);
        scrollableView.setOrientation(LinearLayout.VERTICAL);
        scrollableView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        headerFrame = new FrameLayout(context);
        headerFrame.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Adjust.Header.height
                )
        );
        headerFrame.setClipChildren(false);
        headerFrame.setClipToPadding(false);

        avatarMetaball = new AvatarMetaball(context);

        contentColumn = new LinearLayout(context);
        contentColumn.setBackgroundColor(viewModel.backgroundColor);
        contentColumn.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int contentHeight = contentColumn.getHeight();
                int frameHeight = getHeight();

                scroller.updateBottomEdge(contentHeight - frameHeight);
            }
        });
        contentColumn.setOrientation(LinearLayout.VERTICAL);
        contentColumn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        mediaLayout = new PreventScroll(context);
        contentColumn.addView(mediaLayout);

        View scrollSpacer = new View(context);
        LinearLayout.LayoutParams scrollSpacerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 3000);
        scrollSpacer.setLayoutParams(scrollSpacerParams);


        scrollableView.addView(headerFrame);
        scrollableView.addView(contentColumn);
        scrollableView.addView(scrollSpacer);
        rootFrame.addView(scrollableView);
        setupBackgroundView(context, viewModel);
        setupParticlesView(context, viewModel);
        setupMaskedView(context);
        setupAvatar(context);

        addView(rootFrame);

        headerGeometry.updateScrollOffset(0);
        headerFrame.post(() -> {
            headerGeometry.size = new CartesianCoordinates((float) headerFrame.getWidth(), (float) headerFrame.getHeight());
            handleHeaderGeometryChange();
        });

        setupExpandableAvatar(context);
        setupTextViews(context, viewModel);
        setupButtons(context);
        setupTopButtons(context, viewModel);

        scroller = new PhysicsScroller(this);
        scroller.setListener(new PhysicsScroller.Listener() {
            @Override
            public void onUpdate(float value, float velocity) {
                scrollTo(0, Math.round(value));
            }
            @Override
            public void onEnd() {}
        });

        ProfileView me = this;
        this.post(new Runnable() {
            @Override
            public void run() {
                me.scrollTo(0, Adjust.Header.topMargin);
            }
        });
    }

    private void setupParticlesView(Context context, ProfileViewModel viewModel) {
        FrameLayout particlesView = new FrameLayout(context);
        particlesView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Adjust.Header.height
                )
        );
        stampsController = StampsController.make(getContext(), viewModel.stampColor);
        for (Stamp stamp : stampsController.stamps) {
            particlesView.addView(stamp.viewOnSpring.view);
        }
        giftsController = GiftsController.make(getContext());
        for (Gift gift : giftsController.gifts) {
            particlesView.addView(gift.viewOnSpring.view);
        }
        rootFrame.addView(particlesView);
    }

    private void setupTopButtons(Context context, ProfileViewModel viewModel) {
        TopButton back = new TopButton(context, viewModel.backButtonDrawable);
        int tappableSize = 175;
        int topMargin = 270;
        backLayoutParams = new LayoutParams(tappableSize, tappableSize);
        backLayoutParams.topMargin = topMargin;
        back.setLayoutParams(backLayoutParams);
        rootFrame.addView(back);

        TopButton more = new TopButton(context, viewModel.moreButtonDrawable);
        moreLayoutParams = new LayoutParams(tappableSize, tappableSize);
        moreLayoutParams.topMargin = topMargin;
        moreLayoutParams.gravity = Gravity.RIGHT;
        more.setLayoutParams(moreLayoutParams);
        rootFrame.addView(more);
    }

    public void pushContent(View view) {
        contentColumn.addView(view, contentColumn.getChildCount() - 1);
    }

    public void updateSmallAvatarBitmap(Bitmap avatarBitmap) {
        avatar.setImageBitmap(avatarBitmap);
        if (headerGeometry.size != null) {
            handleHeaderGeometryChange();
        }
    }

    public void updateBigAvatarBitmap(Bitmap avatarBitmap) {
        expandingAvatar.updateAvatar(avatarBitmap);
        if (headerGeometry.size != null) {
            handleHeaderGeometryChange();
        }
    }

    private void handleHeaderGeometryChange() {
        stampsController.handleHeaderGeometryChange(headerGeometry);
        giftsController.handleHeaderGeometryChange(headerGeometry);
        avatar.handleHeaderGeometryChange(headerGeometry);
        expandingAvatar.handleHeaderGeometryChange(headerGeometry);
        textsView.handleHeaderGeometryChange(headerGeometry);
        Path metaballPath = avatarMetaball.handleHeaderGeometryChange(headerGeometry, giftsController.gifts);
        maskedView.setMaskPath(metaballPath);

        float headerHeight = resolveHeaderHeight(headerGeometry);
        backgroundView.setLayoutParams(new LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                (int)headerHeight
        ));
        buttonsLayout.topMargin = (int)headerHeight - Adjust.Header.buttonsBottom;
        buttonsLayoutView.setFadeOut(headerGeometry.scrollDownProgress);
        updateTextsLayout(headerGeometry, headerHeight);
        updateContentLayout(headerGeometry, headerHeight);
        int buttonTop = 270 + headerGeometry.scrollDownOffset.intValue();
        backLayoutParams.topMargin = buttonTop;
        moreLayoutParams.topMargin = buttonTop;
    }

    private void updateContentLayout(HeaderGeometry headerGeometry, float headerHeight) {
        LinearLayout.LayoutParams columnLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int contentTop;
        if (headerGeometry.scrollUpProgress > 0) {
            contentTop = (int)headerHeight - Adjust.Header.height;
        } else {
            contentTop = 200;
        }
        columnLayout.topMargin = contentTop;
        contentColumn.setLayoutParams(columnLayout);
    }

    private void updateTextsLayout(HeaderGeometry headerGeometry, float headerHeight) {
        float scrollUpProgress = headerGeometry.scrollUpProgress;
        if (scrollUpProgress > 0) {
            textsLayout.topMargin = (int)LinearSpace.unit.convert(
                    scrollUpProgress,
                    new LinearSpace(
                            Adjust.Header.regularTextsTop,
                            headerHeight - Adjust.Header.expandedTextsBottom
                    )
            );
        } else {
            textsLayout.topMargin = (int)(
                    Adjust.Header.regularTextsTop +
                            Math.max(0, headerGeometry.scrollDownOffset - 320)
            );
        }
    }

    private float resolveHeaderHeight(HeaderGeometry headerGeometry) {
        if (headerGeometry.scrollUpOffset <= 0) {
            return Adjust.Header.height + Adjust.Header.topMargin + Math.max(0, headerGeometry.scrollDownOffset - 565);
        }

        float curved = TimingFunction.Bezier.easeInOut.execute(headerGeometry.scrollUpProgress);
        return LinearSpace.unit.convert(
                curved,
                new LinearSpace(
                        Adjust.Header.height + Adjust.Header.topMargin,
                        headerGeometry.size.x * Adjust.Header.expandedRatio
                )
        );
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            WindowInsets insets = getRootWindowInsets();
            if (insets != null) {
                avatarMetaball.insetsDidResolve(insets, getContext());
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        headerGeometry.size = new CartesianCoordinates((float) headerFrame.getWidth(), (float) headerFrame.getHeight());
//        headerGeometry.size = new CartesianCoordinates((float)w, (float)h);
        mediaLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h - 240));
        handleHeaderGeometryChange();
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        headerGeometry.updateScrollOffset(scrollY - Adjust.Header.topMargin);
        handleHeaderGeometryChange();
    }

    private void setupAvatar(Context context) {
        avatar = new Avatar(context);
        int avatarSize = (int)(Adjust.Avatar.size * 3);
        FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(
                avatarSize, avatarSize
        );
        avatarParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        avatarParams.topMargin = (int)(Adjust.Header.height * Adjust.Header.verticalCenterShift * 0.5) - (avatarSize / 2);
        avatar.setLayoutParams(avatarParams);
        maskedView.addView(avatar);
    }

    private void setupMaskedView(Context context) {
        maskedView = new MaskedView(context);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
        layoutParams.topMargin = Adjust.Header.topMargin;
        maskedView.setLayoutParams(layoutParams);
        maskedView.setClipChildren(false);
        maskedView.setClipToPadding(false);
        rootFrame.addView(maskedView);
    }

    private void setupBackgroundView(Context context, ProfileViewModel viewModel) {
        backgroundView = new GradientBackgroundView(context, viewModel.centerColor, viewModel.middleColor, viewModel.edgeColor);
        FrameLayout.LayoutParams rectParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                Adjust.Header.height + Adjust.Header.topMargin,
                Gravity.TOP | Gravity.LEFT
        );
        rectParams.leftMargin = 0;
        rectParams.topMargin = 0;
        backgroundView.setLayoutParams(rectParams);
        rootFrame.addView(backgroundView);
    }

    private void setupTextViews(Context context, ProfileViewModel viewModel) {
        textsView = new TextsView(context);
        textsView.setTitle(viewModel.title);
        textsView.setSubtitle(viewModel.subtitle);

        textsLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textsLayout.gravity = Gravity.TOP;
        textsLayout.topMargin = (int)Adjust.Header.regularTextsTop;

        textsView.setLayoutParams(textsLayout);

        rootFrame.addView(textsView);
    }

    private void setupButtons(Context context) {
        buttonsLayoutView = new ButtonsLayout(context);
        buttonsLayout = buttonsLayoutView.getButtonsLayoutParams();
        rootFrame.addView(buttonsLayoutView);
    }

    private void setupExpandableAvatar(Context context) {
        expandingAvatar = new ExpandingAvatar(context, 0, 0, Adjust.Avatar.size / 2, 0, 0, 100, 100);
        FrameLayout.LayoutParams rectParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.LEFT
        );
        rectParams.leftMargin = 0;
        rectParams.topMargin = 0;
        expandingAvatar.setLayoutParams(rectParams);
        rootFrame.addView(expandingAvatar);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scroller.handleTouchEvent(ev);
        return true;
    }
}
