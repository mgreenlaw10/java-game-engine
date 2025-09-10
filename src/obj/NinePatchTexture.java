package src.obj;
import src.obj.ui.Drawable;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import java.awt.Graphics2D;

import java.util.function.BiConsumer;

import src.math.Box2i;

public class NinePatchTexture implements Drawable {

	private BufferedImage sourceImage;
	private BufferedImage scaledImage;

	int w, h; 

	int leftMargin, rightMargin,
		topMargin, bottomMargin;

	public NinePatchTexture(String fPath, int leftMargin, int rightMargin, int topMargin, int bottomMargin) {

		try { sourceImage = ImageIO.read(new File(fPath)); }
		catch (IOException e) { e.printStackTrace(); }

		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
	}
	// implement clone()
	public NinePatchTexture(BufferedImage sourceImage, int leftMargin, int rightMargin, int topMargin, int bottomMargin) {

		this.sourceImage = sourceImage;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
	}

	public void setLeftMargin(int val) {
		leftMargin = val;
		assembleScaledImage();
	}

	public void setRightMargin(int val) {
		rightMargin = val;
		assembleScaledImage();
	}

	public void setTopMargin(int val) {
		topMargin = val;
		assembleScaledImage();
	}

	public void setBottomMargin(int val) {
		bottomMargin = val;
		assembleScaledImage();
	}

	public void setSize(int w, int h) {
		// don't redraw the image if this is the same size as last time
		if (w == this.w && h == this.h) {
			return;
		}
		this.w = w;
		this.h = h;
	    assembleScaledImage();
	}

	private void assembleScaledImage() {

		int srcW = sourceImage.getWidth();
	    int srcH = sourceImage.getHeight();
	    int srcCenterW = srcW - leftMargin - rightMargin;
	    int srcCenterH = srcH - topMargin - bottomMargin;
	    int dstCenterW = Math.max(0, w - leftMargin - rightMargin);
	    int dstCenterH = Math.max(0, h - topMargin - bottomMargin);

	    // calculate patches
	    Box2i SRC_TL = new Box2i(0                 , 0, leftMargin , topMargin);
	    Box2i SRC_TM = new Box2i(leftMargin        , 0, srcCenterW , topMargin);
	    Box2i SRC_TR = new Box2i(srcW - rightMargin, 0, rightMargin, topMargin);
	    Box2i SRC_ML = new Box2i(0                 , topMargin, leftMargin , srcCenterH);
	    Box2i SRC_C  = new Box2i(leftMargin        , topMargin, srcCenterW , srcCenterH);
	    Box2i SRC_MR = new Box2i(srcW - rightMargin, topMargin, rightMargin, srcCenterH);
	    Box2i SRC_BL = new Box2i(0                 , srcH - bottomMargin, leftMargin , bottomMargin);
	    Box2i SRC_BM = new Box2i(leftMargin        , srcH - bottomMargin, srcCenterW , bottomMargin);
	    Box2i SRC_BR = new Box2i(srcW - rightMargin, srcH - bottomMargin, rightMargin, bottomMargin);

	    Box2i DST_TL = new Box2i(0              , 0, leftMargin , topMargin);
	    Box2i DST_TM = new Box2i(leftMargin     , 0, dstCenterW , topMargin);
	    Box2i DST_TR = new Box2i(w - rightMargin, 0, rightMargin, topMargin);
	    Box2i DST_ML = new Box2i(0              , topMargin, leftMargin , dstCenterH);
	    Box2i DST_C  = new Box2i(leftMargin     , topMargin, dstCenterW , dstCenterH);
	    Box2i DST_MR = new Box2i(w - rightMargin, topMargin, rightMargin, dstCenterH);
	    Box2i DST_BL = new Box2i(0              , h - bottomMargin, leftMargin , bottomMargin);
	    Box2i DST_BM = new Box2i(leftMargin     , h - bottomMargin, dstCenterW , bottomMargin);
	    Box2i DST_BR = new Box2i(w - rightMargin, h - bottomMargin, rightMargin, bottomMargin);

	    this.scaledImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = this.scaledImage.createGraphics();

	    drawPatch(SRC_TL, DST_TL, g2); 
	    drawPatch(SRC_TM, DST_TM, g2); 
	    drawPatch(SRC_TR, DST_TR, g2);
	    drawPatch(SRC_ML, DST_ML, g2);
	    drawPatch(SRC_C , DST_C , g2); 
	    drawPatch(SRC_MR, DST_MR, g2);
	    drawPatch(SRC_BL, DST_BL, g2); 
	    drawPatch(SRC_BM, DST_BM, g2); 
	    drawPatch(SRC_BR, DST_BR, g2);

	    g2.dispose();
	}

	private void drawPatch(Box2i srcPatch, Box2i dstPatch, Graphics2D g2) {

		if (srcPatch.width <= 0 || srcPatch.height <= 0 ||
		    dstPatch.width <= 0 || dstPatch.height <= 0) 
				return;

        g2.drawImage (
            sourceImage,
            dstPatch.x, dstPatch.y, dstPatch.x + dstPatch.width, 
            dstPatch.y + dstPatch.height,
            srcPatch.x, srcPatch.y, srcPatch.x + srcPatch.width, 
            srcPatch.y + srcPatch.height,
            null
        );
	}

	@Override
	public BufferedImage getImage() {
		return scaledImage;
	}
}