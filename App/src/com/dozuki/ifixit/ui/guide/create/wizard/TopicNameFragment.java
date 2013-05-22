/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dozuki.ifixit.ui.guide.create.wizard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.dozuki.ifixit.MainApplication;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.guide.wizard.TopicNamePage;
import com.dozuki.ifixit.util.APIEvent;
import com.dozuki.ifixit.util.APIService;
import com.squareup.otto.Subscribe;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.widget.AutoCompleteTextView;

import java.util.ArrayList;

public class TopicNameFragment extends Fragment {
   private static final String ARG_KEY = "key";
   public static final String TOPIC_LIST_KEY = "TOPIC_LIST_KEY";

   private PageFragmentCallbacks mCallbacks;
   private String mKey;
   private TopicNamePage mPage;
   private AutoCompleteTextView mTopicNameView;
   private ArrayAdapter<String> mAdapter;
   private ArrayList<String> mTopics;

   public static TopicNameFragment create(String key) {
      Bundle args = new Bundle();
      args.putString(ARG_KEY, key);
      Log.w("TopicNameFragment", "create");

      TopicNameFragment fragment = new TopicNameFragment();
      fragment.setArguments(args);
      return fragment;
   }

   public TopicNameFragment() { }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.w("TopicNameFragment", "onCreate");

      Bundle args = getArguments();
      mKey = args.getString(ARG_KEY);
      mPage = (TopicNamePage) mCallbacks.onGetPage(mKey);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      if (savedInstanceState != null) {
         mTopics = savedInstanceState.getStringArrayList(TOPIC_LIST_KEY);
      }

      View rootView = inflater.inflate(R.layout.guide_create_intro_topic_name, container, false);
      Log.w("TopicNameFragment", "onCreateView");

      // Set page title
      ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

      // Set page description
      ((TextView) rootView.findViewById(R.id.page_description)).setText(mPage.getDescription());

      mTopicNameView = (AutoCompleteTextView) rootView.findViewById(R.id.topic_name);
      Log.w("TopicNameFragment", "topicNameView initialized");

      if (mTopics != null) {
         setTopicArrayAdapter();
      } else {
         APIService.call(getActivity(), APIService.getAllTopicsAPICall());
      }

      mTopicNameView.setText(mPage.getData().getString(TopicNamePage.TOPIC_DATA_KEY));
      Log.w("TopicNameFragment", "topic nave value set");

      return rootView;
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      Log.w("TopicNameFragment", "onAttach");

      if (!(activity instanceof PageFragmentCallbacks)) {
         throw new ClassCastException("Activity must implement PageFragmentCallbacks");
      }

      mCallbacks = (PageFragmentCallbacks) activity;
   }

   @Override
   public void onDetach() {
      super.onDetach();
      mCallbacks = null;
   }

   @Override
   public void onResume() {
      super.onResume();
      Log.w("TopicNameFragment", "onResume");
      MainApplication.getBus().register(this);
   }

   @Override
   public void onPause() {
      super.onPause();
      Log.w("TopicNameFragment", "onPause");

      MainApplication.getBus().unregister(this);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      Log.w("TopicNameFragment", "onSaveInstanceState");

      outState.putStringArrayList(TOPIC_LIST_KEY, mTopics);
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      mTopicNameView.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1,
          int i2) {
         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
         }

         @Override
         public void afterTextChanged(Editable editable) {
            mPage.getData().putString(TopicNamePage.TOPIC_DATA_KEY,
             (editable != null) ? editable.toString() : null);
            mPage.notifyDataChanged();
         }
      });

   }

   @Override
   public void setMenuVisibility(boolean menuVisible) {
      super.setMenuVisibility(menuVisible);

      // In a future update to the support library, this should override setUserVisibleHint
      // instead of setMenuVisibility.
      if (mTopicNameView != null) {
         InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
          Context.INPUT_METHOD_SERVICE);
         if (!menuVisible) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
         }
      }
   }

   private void setTopicArrayAdapter() {
      mAdapter = new ArrayAdapter<String>((Activity) mCallbacks, android.R.layout.simple_dropdown_item_1line,
       mTopics);

      mTopicNameView.setAdapter(mAdapter);
      Log.w("TopicNameFragment", "Topic list Array adapter set");

   }

   @Subscribe
   public void onTopicList(APIEvent.TopicList event) {
      if (!event.hasError()) {
         mTopics = new ArrayList<String>(event.getResult());
         Log.w("TopicNameFragment onTopicList", mTopics.toString());

         setTopicArrayAdapter();
      } else {
         APIService.getErrorDialog(getActivity(), event.getError(),
          APIService.getAllTopicsAPICall()).show();
      }
   }
}
