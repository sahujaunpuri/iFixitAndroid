package com.dozuki.ifixit.ui.guide.view;

import android.os.Bundle;
import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.guide.Guide;
import com.dozuki.ifixit.ui.WebViewFragment;
import com.dozuki.ifixit.ui.guide.DocumentWebViewFragment;

import java.util.HashMap;
import java.util.Map;

public class GuideViewAdapter extends FixedFragmentStatePagerAdapter {
   private static final int GUIDE_INTRO_POSITION = 0;
   private static final int GUIDE_CONCLUSION_OFFSET = 1;
   private Map<Integer, String> mPageLabelMap;

   private int mStepOffset = 1;

   // Default these to a page number that doesn't exist. They will be updated
   // if the guide has tools and parts.
   private int mToolsPosition = -1;
   private int mPartsPosition = -1;
   private int mConclusionPosition = -1;
   private int mFeaturedDocumentPosition = -1;

   private Guide mGuide;
   private boolean mIsOfflineGuide;

   public GuideViewAdapter(FragmentManager fm, Guide guide, boolean isOfflineGuide) {
      super(fm);
      mGuide = guide;
      mIsOfflineGuide = isOfflineGuide;

      mPageLabelMap = new HashMap<Integer, String>();

      if (mGuide.getFeaturedDocument() != null) {
         mFeaturedDocumentPosition = mStepOffset;
         mStepOffset++;
      }

      if (guideHasTools()) {
         mToolsPosition = mStepOffset;
         mStepOffset++;
      }

      if (guideHasParts()) {
         mPartsPosition = mStepOffset;
         mStepOffset++;
      }

      if (!mGuide.isTeardown()) {
         mConclusionPosition = getCount() - 1;
      }
   }

   @Override
   public int getCount() {
      if (mGuide != null) {
         int count = mGuide.getNumSteps() + mStepOffset;
         if (!mGuide.isTeardown()) {
            count +=  GUIDE_CONCLUSION_OFFSET;
         }
         return count;
      } else {
         return 0;
      }
   }

   @Override
   public Fragment getItem(int position) {
      Fragment fragment;
      String label = "/guide/view/" + mGuide.getGuideid();

      if (position == GUIDE_INTRO_POSITION) {
         label += "/intro";
         fragment = new GuideIntroViewFragment();
         Bundle args = new Bundle();
         args.putSerializable(GuideIntroViewFragment.GUIDE_KEY, mGuide);
         fragment.setArguments(args);
      } else if (position == mFeaturedDocumentPosition) {
         label += "/featured-document";

         fragment = new DocumentWebViewFragment();
         Bundle args = new Bundle();
         args.putString(DocumentWebViewFragment.URL_KEY, mGuide.getFeaturedDocument().getFullUrl().replace(".pdf", ""));
         fragment.setArguments(args);
      } else if (position == mToolsPosition) {
         label += "/tools";
         fragment = GuidePartsToolsViewFragment.newInstance(mGuide.getTools());
      } else if (position == mPartsPosition) {
         label += "/parts";
         fragment = GuidePartsToolsViewFragment.newInstance(mGuide.getParts());
      } else if (position == mConclusionPosition) {
         label += "/conclusion";
         fragment = GuideConclusionFragment.newInstance(mGuide);
      } else {
         int stepNumber = (position - mStepOffset);
         label += "/" + (stepNumber + 1); // Step title # should be 1 indexed.

         Bundle args = new Bundle();
         args.putSerializable("STEP_KEY", mGuide.getStep(stepNumber));
         args.putBoolean("OFFLINE_KEY", mIsOfflineGuide);

         fragment = new GuideStepViewFragment();
         fragment.setArguments(args);
      }

      mPageLabelMap.put(position, label);
      return fragment;
   }

   public String getFragmentScreenLabel(int key) {
      return mPageLabelMap.get(key);
   }

   @Override
   public void destroyItem(View container, int position, Object object) {
      super.destroyItem(container, position, object);

      mPageLabelMap.remove(position);
   }

   @Override
   public CharSequence getPageTitle(int position) {
      if (position == GUIDE_INTRO_POSITION) {
         return App.get().getString(R.string.introduction);
      } else if (position == mFeaturedDocumentPosition) {
         return "Featured Documents";
      } else if (position == mToolsPosition) {
         return App.get().getString(R.string.requiredTools);
      } else if (position == mPartsPosition) {
         return App.get().getString(R.string.requiredParts);
      } else if (position == mConclusionPosition) {
         return App.get().getString(R.string.conclusion);
      } else {
         return App.get().getString(R.string.step_number, (position - mStepOffset) + 1);
      }
   }

   public int getStepOffset() {
      return mStepOffset;
   }

   private boolean guideHasTools() {
      return mGuide.getNumTools() != 0;
   }

   private boolean guideHasParts() {
      return mGuide.getNumParts() != 0;
   }
}
