package com.evolved.ggml;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Evolved_ggmlthreadingh {
    public static final ReentrantLock criticalSection = new ReentrantLock();
    public static final Condition sectionStart = criticalSection.newCondition();
    public static final Condition sectionEnd = criticalSection.newCondition();

    public static void ggml_critical_section_start() {
        criticalSection.lock();
        try {
            sectionStart.signalAll();
        } finally {
            criticalSection.unlock();
        }
    }

    public static void ggml_critical_section_end() {
        criticalSection.lock();
        try {
            sectionEnd.signalAll();
        } finally {
            criticalSection.unlock();
        }
    }
}