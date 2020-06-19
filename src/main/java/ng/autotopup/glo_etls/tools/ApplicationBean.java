package ng.autotopup.glo_etls.tools;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationBean {
	
	private LocalDateTime lastModified ;

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

}
