package qwen3vl;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Evolved_imageprocessorgo {
    private int numChannels;
    private int patchSize;
    private int temporalPatchSize;
    private int mergeSize;
    private int shortestEdge;
    private int longestEdge;
    private float rescaleFactor;
    private float[] imageMean;
    private float[] imageStd;

    public Evolved_imageprocessorgo(int numChannels, int patchSize, int temporalPatchSize, int mergeSize, int shortestEdge, int longestEdge, float rescaleFactor, float[] imageMean, float[] imageStd) {
        this.numChannels = numChannels;
        this.patchSize = patchSize;
        this.temporalPatchSize = temporalPatchSize;
        this.mergeSize = mergeSize;
        this.shortestEdge = shortestEdge;
        this.longestEdge = longestEdge;
        this.rescaleFactor = rescaleFactor;
        this.imageMean = imageMean;
        this.imageStd = imageStd;
    }

    public int[] SmartResize(int height, int width) {
        if (height \u003c= 0 || width \u003c= 0) {
            throw new IllegalArgumentException();
        }
        return new int[]{width, height};
    }

}