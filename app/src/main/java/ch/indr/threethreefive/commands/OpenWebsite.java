package ch.indr.threethreefive.commands;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;

public class OpenWebsite extends PageCommand {
  private final Context mContext;
  private String mWebsiteUri;

  public OpenWebsite(Context context, String websiteUri) {
    mContext = context;
    mWebsiteUri = websiteUri;
  }

  @NonNull @Override
  public String getName() {
    String url = mWebsiteUri.replaceFirst("^https?://", "");
    if (url.endsWith("/")) {
      url = url.substring(0, url.lastIndexOf("/"));
    }
    return "Browse Website " + url;
  }

  @Override
  public String getDescription() {
    return getName();
  }

  @Override
  public void execute(@NonNull Environment environment) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(mWebsiteUri));
    mContext.startActivity(intent);
  }
}
