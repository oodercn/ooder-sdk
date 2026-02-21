/**
 * $RCSfile: Pager.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.web.util;

public class Pager {

	private Integer current;

	private Integer total;

	private Integer length;

	private Integer start;

	private Integer end;

	public Pager(Integer current, Integer total, Integer length) {
		this.current = current;
		this.total = total;
		this.length = length;

		this.start = this.current - this.length / 2;
		if (this.start < 1) {
		   this.start = 1;
		}
		this.end = this.start + this.length - 1;
		if (this.end > this.total) {
			this.end = this.total;

			int tmpStartNum = this.total - this.length + 1;
			if (tmpStartNum < this.start) {
				this.start = tmpStartNum;
				if (this.start < 1) {
					this.start = 1;
				}
			}
		}
	}

	public Integer getCurrent() {
		return current;
	}

	public void setCurrent(Integer current) {
		this.current = current;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
}
