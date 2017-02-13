package webProg2015.project.utilities;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import webProg2015.project.dao.SessionDao;
import webProg2015.project.model.Session;

public class ExpiredSessionsCleanUp implements ServletContextListener {
	
	private ScheduledExecutorService    scheduler    = null;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
		     System.out.println("Scheduler Shutting down successfully " + new Date());
		     scheduler.shutdown();
		  } catch (Exception ex) {
		}		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		if ((scheduler == null) || (!scheduler.isTerminated())) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new SessionCleanUpTask(), 0, 35, TimeUnit.MINUTES);
        }		
	}
}

class SessionCleanUpTask extends TimerTask {
	
    public void run() {
    	SessionDao sDao = SessionDao.getInstance();
    	Calendar c = Calendar.getInstance(); 
    	Date currentTime  = null;
    	Date expiryTime  = null;
    	Collection<Session> allSesions = sDao.readAllSesions();
    	for (Session session : allSesions) {
    		c.setTime(session.getLastLogInTime()); 
    		currentTime = new Date();
			if(session.isPersistent()){
				c.add(Calendar.DATE, 30);
				expiryTime = c.getTime();
				if(expiryTime.before(currentTime)){
					sDao.removeSession(session.getSessionId());
				}
			}else {
				c.add(Calendar.MINUTE, 30);
				expiryTime = c.getTime();
				if(expiryTime.before(currentTime)){
					sDao.removeSession(session.getSessionId());
					System.out.println("Session "+ session.getUsername() +" cleand, last call was at: " + session.getLastLogInTime());
				}
			}
		}
   }
}
