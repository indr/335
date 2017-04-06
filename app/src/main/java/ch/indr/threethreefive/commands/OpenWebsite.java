package ch.indr.threethreefive.commands;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;

public class OpenWebsite extends PageCommand {

  private final Context context;
  private String websiteUri;

  public OpenWebsite(final @NonNull Context context, final @NonNull String websiteUri) {
    super("Open website");
    this.context = context;
    this.websiteUri = websiteUri;

    String url = this.websiteUri.replaceFirst("^https?://", "");
    if (url.endsWith("/")) {
      url = url.substring(0, url.lastIndexOf("/"));
    }
    setTitle("Open website " + url);
  }

  @Override public void execute(@NonNull Environment environment) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(websiteUri));
    context.startActivity(intent);
  }
}
