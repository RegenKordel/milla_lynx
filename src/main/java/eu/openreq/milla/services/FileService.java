package eu.openreq.milla.services;

import eu.openreq.milla.models.json.Dependency;
import eu.openreq.milla.models.json.Dependency_status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;


@Service
public class FileService {

	@Value("${milla.dependencyUpdateLogFile}")
	private String dependencyLog;

	@Value("${milla.requestLogFile}")
	private String restLog;

	public String logDependencies(Collection<Dependency> dependencies) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(dependencyLog, true))) {
			for (Dependency dep : dependencies) {
				if (dep.getStatus()!=null && dep.getStatus().equals(Dependency_status.ACCEPTED)) {
					String acceptedString = "ACCEPTED | " + dep.getFromid() + "_" + dep.getToid() + " | " + dep.getDependency_type() + "\n";
					writer.append(acceptedString);
				}
				if (dep.getStatus()!=null && dep.getStatus().equals(Dependency_status.REJECTED)) {
					String rejectedString = "REJECTED | " + dep.getFromid() + "_" + dep.getToid() + " | " + dep.getDependency_type() + "\n";
					writer.append(rejectedString);
				}
			}
			writer.close();	
			return "Success";
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}

	}

	public String logRequests(HttpServletRequest httpRequest) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(restLog, true))) {
			String uri = httpRequest.getRequestURI();
			String line = "\n" + uri.substring(1, uri.length()) + " | ";
			line += writeParamsToLine(httpRequest.getParameterMap());
			writer.append(line);
			writer.close();
			return "Success";
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}

	public String writeParamsToLine(Map<String, String[]> parameterMap) {
		String line = "";

		for (String key : parameterMap.keySet()) {
			String[] params = parameterMap.get(key);
			line += key + ":";
			for (String param : params) {
				line += param + ",";
			}
			line = line.substring(0, line.length() - 1) + " | ";
		}

		line = line.substring(0, line.length() - 2);

		return line;
	}

	public void setDependencyLogFilePath(String path) {
		this.dependencyLog = path;
	}

	public void setRestLogFilePath(String restLog) { this.restLog = restLog; }

}
