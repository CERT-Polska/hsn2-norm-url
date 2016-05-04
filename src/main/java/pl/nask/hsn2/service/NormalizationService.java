/*
 * Copyright (c) NASK, NCSC
 *
 * This file is part of HoneySpider Network 2.1.
 *
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.service;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;

import pl.nask.hsn2.CommandLineParams;
import pl.nask.hsn2.ServiceMain;
import pl.nask.hsn2.task.TaskFactory;

public class NormalizationService extends ServiceMain{

	public static void main(final String[] args) throws DaemonInitException {
		NormalizationService ns = new NormalizationService();
		ns.init(new DaemonContext() {
			public DaemonController getController() {
				return null;
			}
			public String[] getArguments() {
				return args;
			}
		});
		ns.start();
    }

	@Override
	protected final CommandLineParams newCommandLineParams() {
		CommandLineParams cmd = new CommandLineParams();
		cmd.useDataStoreAddressOption(false);
		cmd.setDefaultServiceNameAndQueueName("norm-url");
		return cmd;
	};

	@Override
	protected void prepareService() {
	}

	@Override
	protected final Class<? extends TaskFactory> initializeTaskFactory() {
		return NormalizationTaskFactory.class;
	}
}