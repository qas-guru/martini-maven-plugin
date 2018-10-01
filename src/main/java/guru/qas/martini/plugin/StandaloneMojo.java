/*
Copyright 2018 Penny Rohr Curich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package guru.qas.martini.plugin;

import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "execute")
public class StandaloneMojo extends AbstractMojo {

	@Parameter(property = "execute.meh")
	private Map meh;

	@Parameter(property = "execute.phrase", defaultValue = "Hallo ${project.version} Welt!")
	private String phrase;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Log log = getLog();
		log.info("From StandaloneMojo");
		log.info(phrase);
		log.info(null == meh ? "null" : meh.toString());
	}
}
