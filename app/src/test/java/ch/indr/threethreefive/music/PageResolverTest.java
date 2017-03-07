/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.music;

import android.net.Uri;

import ch.indr.threethreefive.music.pages.IndexPage;
import ch.indr.threethreefive.navigation.PageMeta;

import static org.junit.Assert.assertEquals;

public class PageResolverTest {

//    @Test
    public void resolvePage() {
        testResolve("/music", IndexPage.class);
    }

    private void testResolve(String uri, Class pageClass) {
        MusicResolver sut = new MusicResolver();
        PageMeta pageMeta = sut.resolve(Uri.parse(uri));
        assertEquals(pageMeta.getClazz(), pageClass);
    }
}
