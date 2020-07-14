package ru.cbr.delta.org.simple_page.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import ru.cbr.delta.delta.profiler.api.pojo.EloUser;
import ru.cbr.delta.profiler.database.service.ProfilerDatabaseService;

@WebServlet(urlPatterns = "/servlet")
public class SimpleServlet extends HttpServlet implements ServiceListener  {
		
		public static String DELTA_PLUGIN_PREFIX = "ru.cbr.delta";
		
		private ServiceReference<ProfilerDatabaseService> databaseReference; 			
		
		
		private ProfilerDatabaseService dbInit(BundleContext context) throws InvalidSyntaxException {
			
			
			context.addServiceListener(this, "(objectClass=" + ProfilerDatabaseService.class.getName() + ")");
			
			databaseReference = (ServiceReference<ProfilerDatabaseService>) context.getServiceReference(ProfilerDatabaseService.class.getName());
			if (databaseReference != null) {
				return context.getService(databaseReference);
				
			}	
			return null;
		}
		
		private void dbDispose(BundleContext context) {
			context.ungetService(databaseReference);			
		}
		
	    @Override
	    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        service(request, response);
	    }

	    @Override
	    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        service(request, response);
	    }

		
		@Override
		protected void service(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			
			response.addHeader("Cache-Control","no-cache");
			response.addHeader("Pragma", "no-cache");
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter pw = response.getWriter();
			
			pw.append("<p>");
			pw.append("Добро пожаловать на главную страницу расширения версии " + FrameworkUtil.getBundle(this.getClass()).getVersion().toString() +"!");
			pw.append("</p>");
			
			ProfilerDatabaseService database;
			try {
				database = dbInit(context);
				if (database != null) {
					List<EloUser> userList = database.readEloUser(null);
					pw.append("<p><h1>");
					if (userList.size() > 0) pw.append("Пользователи:");
					else pw.append("Нет зарегистрированных пользователей!");
					pw.append("</h1></p>");
					for (EloUser user : userList) {
						pw.append(user.getName() + ", " + user.getDepartment() +"<br>");
					}
				} else {
					pw.append("Подсистема управления профилем недоступна!");
				}
				dbDispose(context);
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				pw.append(e.getMessage());
			}
			
			
		}

		@Override
		public void serviceChanged(ServiceEvent event) {
			// TODO Auto-generated method stub
	        switch (event.getType()) {
	    	case ServiceEvent.UNREGISTERING :
	    		//if (database != null) database.stop();
	    		//context.ungetService(event.getServiceReference());
	    		break;
	    	case ServiceEvent.REGISTERED :
	    		//databaseReference = (ServiceReference<ProfilerDatabaseService>) context.getServiceReference(ProfilerDatabaseService.class.getName());
	    		//database = context.getService(databaseReference);
	    	//...
	        break;
	        }
		}
}
