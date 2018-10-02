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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import com.beust.jcommander.JCommander;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import guru.qas.martini.standalone.Main;
import guru.qas.martini.standalone.harness.Options;
import guru.qas.martini.standalone.jcommander.CommandLineOptions;

import static com.google.common.base.Preconditions.*;
import static guru.qas.martini.standalone.jcommander.CommandLineOptions.*;

@SuppressWarnings("WeakerAccess")
public class PluginMain extends Main {

	protected final MavenProject mavenProject;

	public PluginMain(MavenProject mavenProject, Options options) {
		super(options);
		this.mavenProject = checkNotNull(mavenProject, "null MavenProject");
	}

	protected void addOptions(ConfigurableApplicationContext context) {
		ConfigurableEnvironment environment = context.getEnvironment();
		MutablePropertySources sources = environment.getPropertySources();
		List<?> profiles = mavenProject.getActiveProfiles();
		profiles.stream()
			.filter(Profile.class::isInstance)
			.map(Profile.class::cast)
			.map(this::getPropertySource)
			.forEach(sources::addLast);
		super.addOptions(context);
	}

	protected PropertySource getPropertySource(Profile profile) {
		String id = profile.getId();
		String name = String.format("Maven profile %s", id);
		Properties source = profile.getProperties();
		return new PropertiesPropertySource(name, source);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		protected MavenProject mavenProject;
		protected Map<String, String> arguments;

		protected Builder() {
			this.arguments = new LinkedHashMap<>();
		}

		public Builder setMavenProject(MavenProject p) {
			this.mavenProject = p;
			return this;
		}

		public Builder setSpringConfigurationLocations(String[] s) {
			arguments.remove(PARAMETER_CONFIG_LOCATIONS);
			if (null != s) {
				List<String> locations = Arrays.stream(s)
					.filter(Objects::nonNull)
					.map(String::trim)
					.filter(location -> !location.isEmpty())
					.collect(Collectors.toList());
				if (!locations.isEmpty()) {
					String joined = Joiner.on(',').skipNulls().join(s);
					arguments.put(PARAMETER_CONFIG_LOCATIONS, joined);
				}
			}
			return this;
		}

		public Builder setUnimplementedStepsFatal(Boolean b) {
			return setBoolean(PARAMETER_UNIMPLEMENTED_STEPS_FATAL, b);
		}

		public Builder setTimeoutInMinutes(Long l) {
			return setLong(PARAMETER_TIMEOUT_MINUTES, l);
		}

		public Builder setSpelFilter(String s) {
			return setString(PARAMETER_SPEL_FILTER, s);
		}

		public Builder setJobPoolPollIntervalMs(Long l) {
			return setLong(PARAMETER_JOB_POOL_POLL_INTERVAL_MS, l);
		}

		public Builder setJsonOutputFile(File f) {
			arguments.remove(PARAMETER_JSON_OUTPUT_FILE);
			if (null != f) {
				arguments.put(PARAMETER_JSON_OUTPUT_FILE, f.getAbsolutePath());
			}
			return this;
		}

		public Builder setJsonOutputFileOverwrite(Boolean b) {
			return setBoolean(PARAMETER_JSON_OVERWRITE, b);
		}

		public Builder setParallelism(Integer i) {
			arguments.remove(PARAMETER_PARALLELISM);
			if (null != i) {
				arguments.put(PARAMETER_PARALLELISM, String.valueOf(i));
			}
			return this;
		}

		public Builder setAwaitTerminationSeconds(Long l) {
			return setLong(PARAMETER_AWAIT_TERMINATION_SECONDS, l);
		}

		public Builder setGatedMartiniComparatorImplementation(String s) {
			return setString(PARAMETER_GATED_MARTINI_COMPARATOR_IMPL, s);
		}

		public Builder setEngineImplementation(String s) {
			return setString(PARAMETER_ENGINE_IMPL, s);
		}

		public Builder setSuiteIdentifierImplementation(String s) {
			return setString(PARAMETER_SUITE_IDENTIFIER_IMPL, s);
		}

		public Builder setTaskFactoryImplementation(String s) {
			return setString(PARAMETER_TASK_FACTORY_IMPL, s);
		}

		public Builder setUncaughtExceptionHandlerImplementation(String s) {
			return setString(PARAMETER_UNCAUGHT_EXCEPTION_HANDLER_IMPL, s);
		}

		public Builder setGatePollTimeoutMilliseconds(Long l) {
			return setLong(PARAMETER_GATE_MONITOR_POLL_TIMEOUT_MS, l);
		}

		protected Builder setBoolean(String parameter, Boolean b) {
			arguments.remove(parameter);
			if (null != b) {
				arguments.put(parameter, String.valueOf(b));
			}
			return this;
		}

		protected Builder setLong(String parameter, Long l) {
			arguments.remove(parameter);
			if (null != l) {
				arguments.put(parameter, String.valueOf(l));
			}
			return this;
		}

		protected Builder setString(String parameter, String value) {
			arguments.remove(parameter);
			String trimmed = null == value ? "" : value.trim();
			if (!trimmed.isEmpty()) {
				arguments.put(parameter, trimmed);
			}
			return this;
		}

		public PluginMain build() {
			checkState(null != mavenProject, "MavenProject not set");
			Options options = getOptions();
			return new PluginMain(mavenProject, options);
		}

		protected Options getOptions() {
			String[] argv = getArgv();
			CommandLineOptions commandLineOptions = new CommandLineOptions();
			JCommander jCommander = JCommander.newBuilder()
				.acceptUnknownOptions(false)
				.programName(Main.class.getName())
				.addObject(commandLineOptions)
				.build();
			jCommander.parse(argv);
			return commandLineOptions;
		}

		protected String[] getArgv() {
			List<String> asList = Lists.newArrayList();
			arguments.forEach((key, value) -> {
				asList.add(key);
				if (null != value && value.trim().length() > 0) {
					asList.add(value);
				}
			});
			return asList.toArray(new String[0]);
		}
	}
}