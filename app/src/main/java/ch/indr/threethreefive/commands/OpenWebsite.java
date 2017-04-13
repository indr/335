package ch.indr.threethreefive.commands;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import ch.indr.threethreefive.libs.utils.StringUtils;

public class OpenWebsite extends PageCommand {

  private final Context context;
  private Uri websiteUri;

  public OpenWebsite(final @NonNull Context context, final @NonNull String websiteUri) {
    this.context = context;
    this.websiteUri = Uri.parse(websiteUri);

    String niceUrl = websiteUri.replaceFirst("^https?://", "");
    if (niceUrl.endsWith("/")) {
      niceUrl = niceUrl.substring(0, niceUrl.lastIndexOf("/"));
    }
    setTitle("Open website " + niceUrl);
    setSubtitle(null);
    setContentDescription("Open website " + niceUrl);
  }

  public OpenWebsite(final @NonNull Context context, final @NonNull Uri websiteUri,
                     final @NonNull String title, final @Nullable String subtitle,
                     final @NonNull String contentDescription) {
    this.context = context;
    this.websiteUri = websiteUri;

    setTitle(title);
    setSubtitle(StringUtils.isEmpty(subtitle) ? null : subtitle);
    setContentDescription(contentDescription);
  }

  @Override public void execute(@NonNull Environment environment) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(websiteUri);
    context.startActivity(intent);
  }
}
