package link.standen.michael.slideshow.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import link.standen.michael.slideshow.R;
import link.standen.michael.slideshow.model.FileItem;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Uri.class, Environment.class})
public class
FileItemHelperTest {

    private FileItemHelper fileItemHelper;

    @Mock
    private Context mockContext;

    @Mock
    private Resources mockedResources;


    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();

    private File nonExistentDirectory;
    private File existentDirectory;

    @Before
    public void setup() {
        fileItemHelper = new FileItemHelper(mockContext);

        nonExistentDirectory = Mockito.mock(File.class);
        Mockito.when(nonExistentDirectory.exists()).thenReturn(false);

        existentDirectory = storageDirectory.getRoot();

        PowerMockito.mockStatic(Environment.class);
    }

    @Test
    @SmallTest
    public void createFileItem() {
        testFileCreate("testFileCreate", "testDir");
        testFileCreate("", "");
        testFileCreate(" ", " ");
        testFileCreate("$51", "%^&");
        testFileCreate("$51", "file://thisIsMyFullPath");
    }

    private void testFileCreate(String fileName, String dirFullPath) {
        File mockedFile = Mockito.mock(File.class);
        Mockito.when(mockedFile.getName()).thenReturn(fileName);
        Mockito.when(mockedFile.getAbsolutePath()).thenReturn(dirFullPath);

        FileItem tempFile = fileItemHelper.createFileItem(mockedFile);
        assertEquals(fileName, tempFile.getName());
        assertEquals(dirFullPath, tempFile.getPath());
    }

    @Test
    @SmallTest
    public void createGoHomeFileItem() {
        // the external storage directory is available
        Mockito.when(Environment.getExternalStorageDirectory()).thenReturn(existentDirectory);

        String playItemString = "Stuck? Click to go Home";
        when(mockContext.getResources()).thenReturn(mockedResources);
        when(mockContext.getResources().getString(R.string.go_home_folder))
                .thenReturn(playItemString);

        FileItem realItem = fileItemHelper.createGoHomeFileItem();
        assertEquals(playItemString, realItem.getName());
        assertTrue(realItem.getIsSpecial());
        assertEquals(existentDirectory.getAbsolutePath(), realItem.getPath());
        assertEquals(true, realItem.getIsDirectory());
    }

    @Test
    @SmallTest
    public void createPlayFileItem() {
        String playItemString = "Play Slideshow from here";
        when(mockContext.getResources()).thenReturn(mockedResources);
        when(mockContext.getResources().getString(R.string.play_folder))
                .thenReturn(playItemString);

        FileItem realItem = fileItemHelper.createPlayFileItem();
        assertEquals(playItemString, realItem.getName());
        assertTrue(realItem.getIsSpecial());
        assertEquals(false, realItem.getIsDirectory());
    }
}