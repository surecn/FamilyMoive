package com.surecn.moat.base.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.app.Activity;
import android.view.View;

import com.surecn.moat.base.IFindView;

public class Injector {

	public static void injectView(Object obj, Object root) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Annotation[] annotations = field.getAnnotations();
			if (annotations != null) {
				for (Annotation annotation : annotations) {
					if (annotation instanceof InjectView) {
						InjectView injectView = (InjectView) annotation;
						int value = injectView.value();
						if (value != -1) {
							try {
								View view = getViewByRoot(root, value);
								field.set(obj, view);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
						break;
					}
				}
			}
		}
	}

	public static void injectClick(Object obj, Object root) {
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			if (annotations != null) {
				for (Annotation annotation : annotations) {
					if (annotation instanceof InjectClick) {
						InjectClick inject = (InjectClick) annotation;
						int[] value = inject.value();
						if (value != null && value.length > 0) {
							View.OnClickListener listener = (View.OnClickListener) obj;
							try {
								for (int res : value) {
									View view = getViewByRoot(root, res);
									if (view == null) {
										throw new NullPointerException();
									}
									view.setOnClickListener(listener);
								}
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					} else if (annotation instanceof InjectLongClick) {
						InjectClick inject = (InjectClick) annotation;
						int[] value = inject.value();
						if (value != null && value.length > 0) {
							View.OnLongClickListener listener = (View.OnLongClickListener) obj;
							try {
								for (int res : value) {
									View view = getViewByRoot(root, res);
									if (view == null) {
										throw new NullPointerException();
									}
									view.setOnLongClickListener(listener);
								}
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public static View getViewByRoot(Object root, int res) {
		View view = null;
		if (root instanceof Activity) {
			view = ((Activity)root).findViewById(res);
		} else if (root instanceof View) {
			view = ((View)root).findViewById(res);
		} else if (root instanceof IFindView) {
			view = ((IFindView)root).findViewById(res);
		}
		return view;
	}

}