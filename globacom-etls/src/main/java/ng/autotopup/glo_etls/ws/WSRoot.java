package ng.autotopup.glo_etls.ws;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import ng.autotopup.glo_etls.jaxrs.FileService;

@ApplicationPath(value = "/ws")
public class WSRoot extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		// TODO Auto-generated method stub
		
		Set<Class<?>> services = new HashSet<>();
		services.add(FileService.class);
		
		return services;
	}

}
