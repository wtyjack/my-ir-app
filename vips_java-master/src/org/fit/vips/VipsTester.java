/*
 * Tomas Popela, 2012
 * VIPS - Visual Internet Page Segmentation
 * Module - VipsTester.java
 */

package org.fit.vips;

import org.fit.xmltree.DOMTree;

/**
 * VIPS API example application.
 * @author Tomas Popela
 *
 */
public class VipsTester {

	/**
	 * Main function
	 * @param args Internet address of web page.
	 */
	public static void main(String args[])
	{
		// we've just one argument - web address of page
		if (args.length != 1)
		{
			System.err.println("We've just only one argument - web address of page!");
			System.exit(0);
		}

		String url = args[0];

		try
		{	
			
			Vips vips = new Vips();
			// disable graphics output
			vips.enableGraphicsOutput(false);
			// disable output to separate folder (no necessary, it's default value is false)
			vips.enableOutputToFolder(false);
			// set permitted degree of coherence
			vips.setPredefinedDoC(8);
			// start segmentation on page
			vips.startSegmentation(url);
			//DOMTree d = new DOMTree("hahaha");
			//d.iterate();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
