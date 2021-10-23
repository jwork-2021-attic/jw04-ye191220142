package com.anish.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;

//import com.anish.calabashbros.BubbleSorter;
import com.anish.monstermatrix.SelectSorter;
import com.anish.monstermatrix.Monster;
import com.anish.monstermatrix.World;
import com.anish.monstermatrix.getRgb;
import com.anish.monstermatrix.getRandom;

import asciiPanel.AsciiPanel;

public class WorldScreen implements Screen {

    private World world;
    private Monster[][] matrix;
    String[] sortSteps;

    public WorldScreen() throws IOException{
        world = new World();

        matrix = new Monster[16][16];

        getRgb rgb = new getRgb();
        rgb.setRgb();
        int[][] colors = rgb.retRgb();
        int[] ranks = rgb.retRank();

        int[] source = new int[256];
        int[] ranIndex = new int[256];
        for(int i = 0; i < 256; i++){
            source[i] = i;
        }
        getRandom randoms = new getRandom(source, 256);
        ranIndex = randoms.getRandomRes();

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                matrix[i][j] = new Monster(new Color(colors[ranIndex[i*16+j]][0], colors[ranIndex[i*16+j]][1], colors[ranIndex[i*16+j]][2]), ranks[ranIndex[i*16+j]], world);
                world.put(matrix[i][j], i+7, j+5);
            }
        }

        SelectSorter<Monster> b = new SelectSorter<>();
        b.load(matrix);
        b.sort();

        sortSteps = this.parsePlan(b.getPlan());
    }

    private String[] parsePlan(String plan) {
        return plan.split("\n");
    }

    private void execute(Monster[][] matrix, String step) {
        String[] couple = step.split("<->");
        getBroByRank(matrix, Integer.parseInt(couple[0])).swap(getBroByRank(matrix, Integer.parseInt(couple[1])));
    }

    private Monster getBroByRank(Monster[][] matrix, int rank) {
        for (Monster[] line : matrix) {
            for(Monster one : line){
                if (one.getRank() == rank) {
                    return one;
                }
            }
        }
        return null;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {

        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {

                terminal.write(world.get(x, y).getGlyph(), x, y, world.get(x, y).getColor());

            }
        }
    }

    int i = 0;

    @Override
    public Screen respondToUserInput(KeyEvent key) {

        if (i < this.sortSteps.length) {
            this.execute(matrix, sortSteps[i]);
            i++;
        }

        return this;
    }

}
