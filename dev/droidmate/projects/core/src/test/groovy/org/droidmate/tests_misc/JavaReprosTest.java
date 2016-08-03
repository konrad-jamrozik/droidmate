// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

package org.droidmate.tests_misc;

import de.erichseifert.vectorgraphics2d.PDFGraphics2D;
import org.junit.Test;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class JavaReprosTest
{

  /**
   * Repro for: https://github.com/eseifert/vectorgraphics2d/issues/41
   * 
   * See also: https://answers.acrobatusers.com/The-font-LucidaGrande-bad-BBox-Error-opening-OCR-documents-Acrobat-XI-Pro-Mac-q36986.aspx
   */
  
  // Gradle dependency: testCompile 'de.erichseifert.vectorgraphics2d:VectorGraphics2D:0.10'
  @Test
  public void BBoxError() throws IOException
  {
    PDFGraphics2D pdf = new PDFGraphics2D(0.0, 0.0, 100.0, 100.0);
    System.out.println(pdf.getFont());
    // Prints out: java.awt.Font[family=Dialog,name=Dialog,style=plain,size=12]

    try (FileOutputStream file = new FileOutputStream("test1.pdf"))
    {
      file.write(pdf.getBytes());
    }

    // Open test1.pdf with Adobe Acrobat Reader DC (15.016.20045) on Windows 10
    // See a dialog box: "The font 'Dialog.plain' contains bad /BBox."

    pdf.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    System.out.println(pdf.getFont());
    // Prints out: java.awt.Font[family=SansSerif,name=SansSerif,style=bold,size=14]
    try (FileOutputStream file = new FileOutputStream("test2.pdf"))
    {
      file.write(pdf.getBytes());
    }

    // Open test2.pdf with Adobe Acrobat Reader DC (15.016.20045) on Windows 10
    // See a dialog box: "The font 'Dialog.plain' contains bad /BBox."
    // I.e. the message is the same, even though the font was changed.

    // Both test1.pdf and test2.pdf in "Document Propeties -> Fonts" show:
    //   Dialog.plain
    //     Type: TrueType
    //     Encoding: Ansi
    //     Actual font: Adobe Sans MM
    //     Actual Font Type: Type 1
  }
}

