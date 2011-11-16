/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javan.app.model;

/**
 *
 * @author 144key
 */
public class BapasObject {
    /** ATTRIBUTES **/
        private int id;
        private String kode;
        private String nama;
    /** METHODS **/
    public BapasObject(int id, String kode, String nama) {
        this.id = id;
        this.kode = kode;
        this.nama = nama;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }
}
