![image](https://github.com/dirtyhub/WaveHeartRate/blob/master/22.gif)

I hope it will inspire you guy to program much better application, and enjoy it, just we togother make progress.

<code>

    /**the view width as 9 weights*/
    private static final int WIDTH_TOTAL_WEIGHTS = 9;
    /**the view height as 9 weights*/
    private static final int HEIGHT_TOTAL_WEIGHTS = 9;
    
    
    //The peak point, all of numbers are weight, such as (4/total_width_weight)*width, (0/total_height_weight)*height
    //(8,7) to (6,0)
    
    //need to draw line by other software
    
    //draw the wave peak when the valid heartrate is gotten.
    //LLL 0  1 2 3 4 5 6 7 8 9
    //0                    (7,0)[2]
    //1				  				
    //2							
    //3								
    //4							
    //5							
    //6			   				
    //7	  (0, 6)[5]	 (4,6)[4]	
    //8	
    //9				          (8,6)[1]  	 
    //10				(5,9)[3]
				
    
    //here you can change the weight to make difference shape of wave
    private static final int[][] m_WavePeak_Weight = new int[][]{
            new int[]{4, 0, 6, 6},
            new int[]{5, 4, HEIGHT_TOTAL_WEIGHTS, 6},
            new int[]{7, 5, 0, HEIGHT_TOTAL_WEIGHTS},
            new int[]{WIDTH_TOTAL_WEIGHTS, 7, 6, 0}
    };

    //draw the line when the heartrate is not gotten.
    
    //LLL 0  1 2 3 4 5 6 7 8 9
    //0                       
    //1				  				
    //2			
    //3			
    //4			
    //5			
    //6			   
    //7	 (0, 6)[2]<<<<<<<<<<<<(8, 6)[1]	
    //8	
    //9				          	 
    //10				
    private static final int[] m_WaveLine_Weight = new int[]{WIDTH_TOTAL_WEIGHTS, 0, 6, 6};

</code>
