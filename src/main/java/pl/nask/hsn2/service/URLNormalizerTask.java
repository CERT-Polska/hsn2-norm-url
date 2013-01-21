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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.TaskContext;
import pl.nask.hsn2.normalizers.UrlNormalizer;
import pl.nask.hsn2.task.Task;

public class URLNormalizerTask implements Task {
	protected static final Logger LOG = LoggerFactory.getLogger(URLNormalizerTask.class);
	
	private String url;
	private TaskContext jobContext;

	
	public URLNormalizerTask(TaskContext job, ServiceData data){
		this.url = data.getInputUrl();
		this.jobContext = job;
	}

	/**
	 * adds following attributes to job
	 * url_normalized - normalized URL
	 * url_normalized_&lt<i>attr</i>&gt - where &lt<i>attr</i>&gt is:
	 * path, tld, port, query, host, protocol
	 *
	 */
	@Override
	public void process() throws ParameterException, ResourceException, StorageException {
		UrlNormalizer normalizer = new UrlNormalizer(url);
		try {
			LOG.info("processing {} , jobId={}", url, this.jobContext.getJobId());
			normalizer.normalize();
		}catch (Exception e) {
			LOG.warn("cannot normalize URL: {}, {} ", url, e.getMessage());
			LOG.trace("cannot normalize url", e);
			jobContext.addWarning("Exception while processing URL:"+url+","+e.getMessage());
		}
		
		if (normalizer.isNormalized()){
			addNormalizedURLAttributesToContext(normalizer);
		} else {
			LOG.warn("incorrect URL: {}",normalizer.getOriginalURL());
			jobContext.addWarning("incorrect URL: " + normalizer.getOriginalURL());
		}
	}

	@Override
	public boolean takesMuchTime() {
		return false;
	}
	private void addNormalizedURLAttributesToContext(UrlNormalizer norm) {
		jobContext.addAttribute("url_normalized", norm.getNormalized());
		jobContext.addAttribute("protocol",norm.getProtocol());
		jobContext.addAttribute("path",norm.getPath());
		if(!norm.isURL())
			return;
		jobContext.addAttribute("host",norm.getHost());
		if(norm.getPort() > 0)
			jobContext.addAttribute("port",norm.getPort());
		if(norm.getTLD().length() > 0)
			jobContext.addAttribute("tld",norm.getTLD());
		if(norm.getSLD().length() > 0)
			jobContext.addAttribute("sld", norm.getSLD());
		if(norm.getQuery()!=null)
			jobContext.addAttribute("query", norm.getQuery());
	}

}
