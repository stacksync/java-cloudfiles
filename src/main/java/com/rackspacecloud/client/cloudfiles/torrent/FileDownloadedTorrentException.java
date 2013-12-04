package com.rackspacecloud.client.cloudfiles.torrent;

public class FileDownloadedTorrentException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6944073255782665786L;
	
	private byte[] torrent;
	
	
	public FileDownloadedTorrentException(byte[] torrent, String message){
		super(message);
		this.torrent = torrent;
	}

	public byte[] getTorrent() {
		return torrent;
	}

	public void setTorrent(byte[] torrent) {
		this.torrent = torrent;
	}

}
