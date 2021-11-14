package com.wj.audioproject.utils.timer;

import java.util.ArrayList;

public class CopyOnWriteArray<T> {
  private ArrayList<T> mData = new ArrayList<T>();
  private ArrayList<T> mDataCopy;

  private final Access<T> mAccess = new Access<T>();

  private boolean mStart;

  static class Access<T> {
    private ArrayList<T> mData;
    private int mSize;

    public T get(int index) {
      return mData.get(index);
    }

    public int size() {
      return mSize;
    }
  }

  public CopyOnWriteArray() {
  }

  private ArrayList<T> getArray() {
    if (mStart) {
      if (mDataCopy == null) mDataCopy = new ArrayList<T>(mData);
      return mDataCopy;
    }
    return mData;
  }

  public Access<T> start() {
    if (mStart) {
        throw new IllegalStateException("Iteration already started");
    }
    mStart = true;
    mDataCopy = null;
    mAccess.mData = mData;
    mAccess.mSize = mData.size();
    return mAccess;
  }

  public void end() {
    if (!mStart) {
        throw new IllegalStateException("Iteration not started");
    }
    mStart = false;
    if (mDataCopy != null) {
      mData = mDataCopy;
      mAccess.mData.clear();
      mAccess.mSize = 0;
    }
    mDataCopy = null;
  }

  public int size() {
    return getArray().size();
  }

  public void add(T item) {
    getArray().add(item);
  }

  public void addAll(CopyOnWriteArray<T> array) {
    getArray().addAll(array.mData);
  }

  public void remove(T item) {
    getArray().remove(item);
  }

  public void clear() {
    getArray().clear();
  }
}