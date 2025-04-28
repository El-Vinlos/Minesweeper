package com.elvinlos.minesweeper.util;

import java.util.Random;

public class Generator {
    public static int[][] generate(int bombNumber, final int width, final int height)
    {
        Random r = new Random();

        int [][] grid = new int[width][height];
        for (int x = 0; x < width; x++){
            grid[x] = new int[height];
        }

        while (bombNumber > 0){
            int x = r.nextInt(width);
            int y = r.nextInt(height);

            // -1 is a bomb
            if (grid[x][y] != -1){
                grid[x][y] = -1;
                bombNumber--;
            }
        }
        grid = calculateNeighbors(grid, width, height);

        return grid;
    }

    private static int[][] calculateNeighbors(int[][] grid, final int width, final int height){
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                grid[x][y] = getNeighborsNumber(grid, x, y, width, height);
            }
        }

        return grid;
    }

    private static int getNeighborsNumber(final int grid[][], final int x, final int y, final int width, final int height) {
        if (grid[x][y] == -1){
            return -1;
        }

        int count = 0;

        if(isMineAt(grid, x - 1, y + 1, width, height)) count++; //top left
        if(isMineAt(grid, x, y + 1, width, height)) count++;        // top
        if(isMineAt(grid, x + 1, y + 1, width, height)) count++; // top right
        if(isMineAt(grid, x - 1, y, width, height)) count++;        // left
        if(isMineAt(grid, x + 1, y, width, height)) count++;        // right
        if(isMineAt(grid, x - 1, y - 1, width, height)) count++; // bottom left
        if(isMineAt(grid, x, y - 1, width, height)) count++;        // bottom
        if(isMineAt(grid, x + 1, y - 1, width, height)) count++; // bottom right

        return count;
    }

    private static boolean isMineAt(final int[][] grid, final int x, final int y, final int width, final int height)
    {
        // Check boundaries
        if (x >= 0 && y >= 0 && x < width && y < height){
            if(grid[x][y] == -1){
                return true;
            }
        }
        return false;
    }
}
