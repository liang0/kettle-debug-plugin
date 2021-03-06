package org.kettle.xp;

import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobAdapter;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransAdapter;

import java.util.concurrent.TimeUnit;

@ExtensionPoint(
  id = "LogJobExecutionTimeExtensionPoint",
  description = "Logs execution time of a job when it finishes",
  extensionPointId = "JobStart"
)
/**
 * set the debug level right before the step starts to run
 */
public class LogJobExecutionTimeExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object o ) throws KettleException {
    if (!(o instanceof Job )) {
      return;
    }

    Job job = (Job) o;

    // If the KETTLE_DEBUG_DURATION variable is set to N or FALSE, we don't log duration
    //
    String durationVariable = job.getVariable( Defaults.VARIABLE_KETTLE_DEBUG_DURATION, "Y" );
    if ("N".equalsIgnoreCase( durationVariable ) || "FALSE".equalsIgnoreCase( durationVariable)) {
      // Nothing to do here
      return;
    }

    final long startTime = System.currentTimeMillis();

    job.addJobListener( new JobAdapter() {
      @Override public void jobFinished( Job job ) throws KettleException {
        long endTime = System.currentTimeMillis();
        double seconds = ((double)endTime - (double)startTime) / 1000;
        log.logBasic("Job duration : "+ seconds+" seconds [ "+ DebugLevelUtil.getDurationHMS( seconds ) +" ]");

      }
    } );
  }
}
