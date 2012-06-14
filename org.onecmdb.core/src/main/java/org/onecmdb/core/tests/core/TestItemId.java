/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.core.tests.core;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.onecmdb.core.internal.model.ItemId;

/*
 * Test the ItemId id generation so it's unique.
 * 
 */
public class TestItemId extends TestCase {

    
    /**
     * Simple encapsualtion for statistics calculation using running sums, that
     * is, sample values are forgotten once they are added to det data set. 
     * <p>For each sample, in the population, just make a call to 
     * {@link #addSample(double)}. Then, just ask for the statistical value
     * interseted in.</p>
     */
    private static class DataSet {
        private final String name;
        private long N = 0; // number of samples
        private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        
        private double sum = 0.0;  // sum
        private double sum2 = 0.0; // square sum
        
        /**
         * <p>Creates a new data set and gives it a suitable name.</p>
         * 
         * <p>The name is used to diffrentiate data sets from each other when
         * serveral data sets are used concurrently, or when nested nested data
         * sets ore in use.</p> 

         * @param name The name for the data set
         */
        public DataSet(String name) {
            this.name = name;
        }

        /**
         * Adds a sample to this calculation. Internally running sums are 
         * updated, as well as min and max, etc.
         * @param x
         */
        public void addSample(double x) {
            N++;
            sum += x;
            sum2 += x * x;

            min = min(min, x);
            max = max(max, x);
            
        }

        /** 
         * The mean value on the current data set.
         * @return
         */
        double getMean() {
            return getTotal() / getSamples();
        }
        
        /**
         * Number of samples added
         * @return
         */
        public long getSamples() {
            return N;
        }
        
        /**
         * The sum of all samples in the current data set.
         * @return
         */
        public double getTotal() {
            return sum;
        }
        /**
         * The sum of all <em>sample squares</em> of the current data set.
         * @return
         */
        public double getSqrTotal() {
            return sum2;
            
        }

        /**
         * Convienent method to view the statistics n the current data set. 
         */
        public String toString() {
            return "["+name + getSamples() + "] sum="+getTotal() + ";avg=" + getMean() + ";std="+getStdDev() + ";min="+min+";max="+max;
        }

        /**
         * Clears the built up values in this data set, suitable when creating 
         * a new data set is not needed. 
         */
        public void reset() {
            N = 0;
            sum = 0.0;
            sum2 = 0.0;
            max = Double.MIN_VALUE;
            min = Double.MAX_VALUE;
        }

        /**
         * The standard deviation for this data set
         * @return
         */
        double getStdDev() {
            return sqrt(getVariance()); 
        }

        /**
         * The standard deviation for this data set
         * @return
         */
        public double getVariance() {
            return (N * sum2 - pow(sum, 2)) / (N * (N -1)); 
        }

        /**
         * The maximum value in this data set
         * @return
         */
        public double getMaximum() {
            return this.max;
        }

        /**
         * The minimum value in this data set
         * @return
         */
        public double getMinimum() {
            return this.min;
        }
    }
    
    
    static class Timing {
        
        final long snapshotN;
        private final long snapshotTimeout;

        private int splits;

        /**
         * A class used to time certain events and collect statistics. Every
         * time a timing takes palace, i.e. a sample, a call to {@link #time}
         * should be used.
         * 
         * Each call to <tt>time</tt> may dicatate that a split should be taken.
         * If a snap should occur depends on the spit
         * 
         * <p>A snapshot is taken for every snapshotN sample, or if the elapsed 
         * time since the last snapshot more than the snapshotTimeout, whichever
         * occurs first.</p>
         * 
         * 
         * @param snapshot Max number of samples between snapshots
         * @param timeout Maximum time in between snapshots. 
         */
        Timing(long snapshotN, long snapshotTimeout) {
            this.snapshotN = snapshotN;
            this.snapshotTimeout = snapshotTimeout;
        }

        final DataSet total = new DataSet("TOTAL");
        final DataSet split = new DataSet("LAST");

        
        /**
         * Executes one <em>round</em>, or sample, of work and updates statistics
         * regarding elapased time for it to complete. The idea is to time
         * the same work several times to build up reliable statistics.
         * 
         * <p>If a snapshot is reached, infomration is printed on stdout.</p>
         * 
         * @param work The work to be messured.
         */
        void time(Runnable work) {
            
            long start = System.currentTimeMillis();
            work.run();
            long delta = System.currentTimeMillis() - start; 
            
            total.addSample(delta);
            split.addSample(delta);
            
            if ( split.getSamples() == snapshotN || (split.getTotal()  >= snapshotTimeout )) {
                splits++;
                System.out.println(this);
                split.reset();
            }
        }
        
        void stop() {
            if (split.getSamples() != 0) {
                System.out.println(this);
            }
        }
        
        public String toString() {
            return total + " " + split;
        }

        public void reset() {
            total.reset();
            split.reset();
            System.out.println(this);
            
        }

        public DataSet getTotal() {
            return total;
        }
    }

    final int N = 5000;
    final int workN = 1000;
    public void testReinstantiate() {
        Timing stat = new Timing(140000, 2800);

        final Runnable w1 = new Runnable() {
            public void run() {
                for (int i = 0; i < workN; i++) {
                ItemId id = new ItemId("fedca9876543210");
                String s = id.toString();
                ItemId id2 = new ItemId(s);
                assertEquals(id, id2);
                }
            }
        };
        for (int n = 0; n < N; n++ ) {
            stat.time(w1);
        }
        System.out.println("-- TOTAL: " + N + "x" + workN + " --");
        stat.stop();
    
        assertTrue("ID reinstantiaion", stat.getTotal().getMean() / 1000 < 3.0 );
    }

    public void testCreateNew() {
        Timing stat = new Timing(140000, 2800);
        final Runnable w2 = new Runnable() {
            public void run() {
                for (int i = 0; i < workN; i++) {
                ItemId id = new ItemId();
                String s = id.toString();
                ItemId id2 = new ItemId(s);
                assertEquals(id, id2);
                }
            }
        };
        for (int n = 0; n < N; n++ ) {
            stat.time(w2);
        }
        System.out.println("-- TOTAL: " + N + "x" + workN + " --");
        stat.stop();
        assertTrue("ID reinstantiaion", stat.getTotal().getMean() / 1000 < 3.0 );
    }
    
    

    public void testStatistics() {
        
        DataSet s = new DataSet("TEST");
        s.addSample(5);
        s.addSample(6);
        s.addSample(3);
        s.addSample(4);
        
       
        
        double stddev = Math.pow( 5 - 4.5, 2); 
        stddev += Math.pow( 6 - 4.5, 2);
        stddev += Math.pow( 3 - 4.5, 2);
        stddev += Math.pow( 4 - 4.5, 2);
        stddev = Math.sqrt(stddev / 3);
        System.out.println("Calced Std Dev:" + stddev);
        
        
        System.out.println("Mean: " + s.getMean());
        System.out.println("Std Dev: " + s.getStdDev());
        System.out.println("Variance: " + s.getVariance());
        System.out.println("Total: " + s.getTotal());
        System.out.println("Maximum: " + s.getMaximum());
        System.out.println("Minimum: " + s.getMinimum());
    }
    
    public void testDuplicates() {
        final int outsideN = 30;
        final int insideN = 10000;
        
        Timing stat = new Timing(5000, 5000);

        class TestData {
           int n = 0;
           int dups = 0; 
        };
        
        class Stuff {
            final long added = System.currentTimeMillis();
            final ItemId id;
            final int iteration;
        
            Stuff(int n, ItemId id) {
                this.iteration = n;
                this.id = id; 
            }
        }
        
        final Map<Long, Stuff> ids = new HashMap<Long,Stuff>(); //(int) ((outsideN * insideN) / 0.75));
        final TestData testdata = new TestData();
        
        
        final Runnable work = new Runnable() {
            public void run() {
                for (int i = 0; i < insideN ; i++) {
                    testdata.n++;

                    ItemId id = new ItemId();
                    Long l = id.asLong();
                    
                    Stuff dup = ids.put(l, new Stuff((testdata.n * insideN + i), id));
                    assertEquals(testdata.n, ids.size());
                    
                    
                    if (dup != null) {
                        assertFalse( dup.id.getDelegate().equals(id.getDelegate()) );
                        
                        System.err.println("duplicate entry in map:\n"
                                + " Last occurence @ iteration# " + dup.iteration + ";id=" + dup.id.getDelegate() + "\n"  
                                + " This occurence @ iteration# " + (testdata.n * insideN + i) +";id=" + id.getDelegate()); 
                        testdata.dups++;
                    }
                    assertTrue("Less than 20 duplicates allowed: ", testdata.dups < 20);
                }
            }
        };
        for (int i = 0; i < outsideN; i++) {
            stat.time(work);
        }
        System.out.println("-- TOTAL: " + outsideN + "x" + insideN + " --");
        stat.stop();
        assertEquals(0, testdata.dups);
    }
    
    
    

}
