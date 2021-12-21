package com.mita.fmipaschedule.model;

public class ScheduleModel {
    // by input
    private String id, semester, code, wp, kelas;
    private MatkulModel matkul;
    private ProgramModel program;
    private int sks, day;
    // by system
    private long timeLong;
    private String fakultas, dosenId;
    private long timeStart, timeEnd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWp() {
        return wp;
    }

    public void setWp(String wp) {
        this.wp = wp;
    }

    public String getDosenId() {
        return dosenId;
    }

    public void setDosenId(String dosenId) {
        this.dosenId = dosenId;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public MatkulModel getMatkul() {
        return matkul;
    }

    public void setMatkul(MatkulModel matkul) {
        this.matkul = matkul;
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        this.sks = sks;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(long timeLong) {
        this.timeLong = timeLong;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public ProgramModel getProgram() {
        return program;
    }

    public void setProgram(ProgramModel program) {
        this.program = program;
    }
}
