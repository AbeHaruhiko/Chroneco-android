package jp.caliconography.chroneco.activity;

import com.parse.ui.ParseLoginDispatchActivity;

public class LoginDispatchActivity extends ParseLoginDispatchActivity {
  @Override
  protected Class<?> getTargetClass() {
    return MemberListActivity.class;
  }
}