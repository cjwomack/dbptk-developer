/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/db-preservation-toolkit
 */
package com.databasepreservation.siarddk;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.testng.annotations.Test;

/*
 * @author Thomas Kristensen tk@bithuset.dk
 */

@Test(groups = {"siarddk"})
public class TestSIARDDKImportModule {
  private final String ARCHIVE_FLD_NAME_SPLIT_TEST = "AVID.TST.4000.1";
  private final String ARCHIVE_FLD_NAME_HEX_TEST = "AVID.HEX.2000.1";

  @Test
  public void testArchiveSplitInMultipleFolders() throws IOException {

    // Test the SIRADDK import modules ability to read archive split in multiple
    // folders, by exporting such a archive to SIRADDK export module, which will
    // consolidate it in a single archive folder.
    // We'll then compare this folder to a folder representing the expected
    // result.

    Path splittedArchiveFld = new File(
      this.getClass().getClassLoader().getResource("siarddk/AVID.SA.18001.1").getFile()).toPath();

    Path generatedArchiveFullPath = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"),
      ARCHIVE_FLD_NAME_SPLIT_TEST);

    Path expectedConsolidatedArchivePath = new File(
      this.getClass().getClassLoader().getResource("siarddk/AVID.TST.4000.1").getFile()).toPath();

    SIARDDKTestUtil.assertArchiveFoldersEqualAfterExportImport(splittedArchiveFld, expectedConsolidatedArchivePath,
      generatedArchiveFullPath);

    // Conduct the very same test, only using relative path for the import
    // archive.

    Path currentWorkingDir = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
    Path splittedArchiveFldRelPath = currentWorkingDir.relativize(splittedArchiveFld);
    SIARDDKTestUtil.assertArchiveFoldersEqualAfterExportImport(splittedArchiveFldRelPath,
      expectedConsolidatedArchivePath, generatedArchiveFullPath);

  }

  @Test
  public void testArchiveWithXmlHexBinaryData() throws IOException {
    // Please notice, that the siard-dk export module does not produce archives
    // with data encoded in hex. The functionality is added to support archives
    // with this type of data encoding generated by other tools.

    // AVID.HEX.1000.1 becomes AVID.HEX.2000.1

    Path hexArchiveFld = new File(this.getClass().getClassLoader().getResource("siarddk/AVID.HEX.1000.1").getFile())
      .toPath();

    Path generatedArchiveFullPath = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"),
      ARCHIVE_FLD_NAME_HEX_TEST);

    Path expectedConsolidatedArchivePath = new File(
      this.getClass().getClassLoader().getResource("siarddk/AVID.HEX.2000.1").getFile()).toPath();

    SIARDDKTestUtil.assertArchiveFoldersEqualAfterExportImport(hexArchiveFld, expectedConsolidatedArchivePath,
      generatedArchiveFullPath);

  }

}
