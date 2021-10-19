package com.anish.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;

//import com.anish.calabashbros.BubbleSorter;
import com.anish.calabashbros.SelectSorter;
import com.anish.calabashbros.Monster;
import com.anish.calabashbros.World;
import com.anish.calabashbros.getRgb;
import com.anish.calabashbros.getRandom;

import asciiPanel.AsciiPanel;

public class WorldScreen implements Screen {

    private World world;
    private Monster[][] bros;
    String[] sortSteps;

    public WorldScreen() throws IOException{
        world = new World();

        bros = new Monster[16][16];

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
                bros[i][j] = new Monster(new Color(colors[ranIndex[i*16+j]][0], colors[ranIndex[i*16+j]][1], colors[ranIndex[i*16+j]][2]), ranks[ranIndex[i*16+j]], world);
                world.put(bros[i][j], i+7, j+5);
            }
        }

        SelectSorter<Monster> b = new SelectSorter<>();
        b.load(bros);
        b.sort();

        sortSteps = this.parsePlan(b.getPlan());
    }

    private String[] parsePlan(String plan) {
        return plan.split("\n");
    }

    private void execute(Monster[][] bros, String step) {
        String[] couple = step.split("<->");
        getBroByRank(bros, Integer.parseInt(couple[0])).swap(getBroByRank(bros, Integer.parseInt(couple[1])));
    }

    private Monster getBroByRank(Monster[][] bros, int rank) {
        for (Monster[] line : bros) {
            for(Monster bro : line){
                if (bro.getRank() == rank) {
                    return bro;
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
            this.execute(bros, sortSteps[i]);
            i++;
        }

        return this;
    }

}
