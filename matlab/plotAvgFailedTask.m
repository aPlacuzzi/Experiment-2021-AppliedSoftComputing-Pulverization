function [] = plotAvgFailedTask(baseDir, baseFileName)

    plotGenericResult(1, 2, 'Failed Tasks (%)', 'ALL_APPS', 'percentage_for_all', baseDir, strcat(baseFileName, 'Failed_Tasks'));
    
    plotGenericResult(2, 2, 'Failed Tasks on Edge (%)', 'ALL_APPS', 'percentage_for_all', baseDir, strcat(baseFileName, 'Failed_Tasks_on_Edge'));
    
    plotGenericResult(3, 2, 'Failed Tasks on Cloud (%)', 'ALL_APPS', 'percentage_for_all', baseDir, strcat(baseFileName, 'Failed_Tasks_on_Cloud'));
    
end