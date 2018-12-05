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

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mojo(name = "execute")
public class StandaloneMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Parameter(property = "execute.spring.configuration.locations")
	private String[] springConfigurationLocations;

	@Parameter(property = "execute.unimplemented.steps.fatal")
	private Boolean unimplementedStepsFatal;

	@Parameter(property = "execute.timeout.in.minutes")
	private Long timeoutInMinutes;

	@Parameter(property = "execute.spel.filter")
	private String spelFilter;

	@Parameter(property = "execute.job.pool.poll.interval.milliseconds")
	private Long jobPoolPollIntervalMs;

	@Parameter(property = "execute.json.output.file")
	private File jsonOutputFile;

	@Parameter(property = "execute.json.output.file.overwrite")
	private Boolean jsonOutputFileOverwrite;

	@Parameter(property = "execute.parallelism")
	private Integer parallelism;

	@Parameter(property = "execute.await.termination.seconds")
	private Long awaitTerminationSeconds;

	@Parameter(property = "execute.engine.implementation")
	private String engineImplementation;

	@Parameter(property = "execute.suite.identifier.implementation")
	private String suiteIdentifierImplementation;

	@Parameter(property = "execute.task.factory.implementation")
	private String taskFactoryImplementation;

	@Parameter(property = "execute.uncaught.exception.handler.implementation")
	private String uncaughtExceptionHandlerImplementation;

	@Parameter(property = "execute.gate.poll.timeout.milliseconds")
	private Long gatePollTimeoutMilliseconds;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			PluginMain main = PluginMain.builder()
				.setMavenProject(project)
				.setSpringConfigurationLocations(springConfigurationLocations)
				.setUnimplementedStepsFatal(unimplementedStepsFatal)
				.setTimeoutInMinutes(timeoutInMinutes)
				.setSpelFilter(spelFilter)
				.setJobPoolPollIntervalMs(jobPoolPollIntervalMs)
				.setJsonOutputFile(jsonOutputFile)
				.setJsonOutputFileOverwrite(jsonOutputFileOverwrite)
				.setParallelism(parallelism)
				.setAwaitTerminationSeconds(awaitTerminationSeconds)
				.setEngineImplementation(engineImplementation)
				.setSuiteIdentifierImplementation(suiteIdentifierImplementation)
				.setTaskFactoryImplementation(taskFactoryImplementation)
				.setUncaughtExceptionHandlerImplementation(uncaughtExceptionHandlerImplementation)
				.setGatePollTimeoutMilliseconds(gatePollTimeoutMilliseconds)
				.build();
			main.executeSuite();
		}
		catch (Exception e) {
			throw new MojoExecutionException("unable to execute suite", e);
		}
	}
}
