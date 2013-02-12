/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
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

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;

import pl.nask.hsn2.CommandLineParams;
import pl.nask.hsn2.GenericService;

public class NormalizationService implements Daemon{

    

    private GenericService service = null;
    private Thread serviceWorker = null;

	public static void main(String[] args) throws DaemonInitException, Exception {
		NormalizationService ns = new NormalizationService();
		ns.init(new JsvcArgsWrapper(args));
		ns.start();
		Thread.currentThread().join();
		ns.stop();
		ns.destroy();
		
    }

	@Override
	public void init(DaemonContext context) throws DaemonInitException,
	Exception {
		CommandLineParams cmd = new CommandLineParams();
		cmd.useDataStoreAddressOption(false);
		cmd.setDefaultServiceNameAndQueueName("norm-url");
		cmd.parseParams(context.getArguments());

		this.service = new GenericService(new NormalizationTaskFactory(), cmd.getMaxThreads(), cmd.getRbtCommonExchangeName(), cmd.getRbtNotifyExchangeName());
		cmd.applyArguments(service);
	}

	@Override
	public void start() throws Exception {
		serviceWorker  = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
						
						@Override
						public void uncaughtException(Thread t, Throwable e) {
							System.exit(1);
							
						}
					} );
					service.run();
				} catch (InterruptedException e) {
					System.exit(0);
				}
				
			}
		},"Normalization-Service");
		serviceWorker.start();
	}

	@Override
	public void stop() throws Exception {
		serviceWorker.interrupt();
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	
	private static class JsvcArgsWrapper implements DaemonContext {
		private String params[];
		public JsvcArgsWrapper(String [] p) {
			params = p;
		}
		@Override
		public DaemonController getController() {
			return null;
		}

		@Override
		public String[] getArguments() {
			return params;
		}
		
	}


}
