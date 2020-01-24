package data;

public class Sort {

//	public static void mergeSort(Drawable[] a, int n) {
//		if (n < 2) {
//			return;
//		}
//		
//		int mid = n / 2;
//		Drawable[] l = new Drawable[mid];
//		Drawable[] r = new Drawable[n - mid];
//
//		for (int i = 0; i < mid; i++) {
//			l[i] = a[i];
//		}
//		
//		for (int i = mid; i < n; i++) {
//			r[i - mid] = a[i];
//		}
//		
//		mergeSort(l, mid);
//		mergeSort(r, n - mid);
//
//		merge(a, l, r, mid, n - mid);
//	}
//
//	public static void merge(Drawable[] a, Drawable[] l, Drawable[] r, int left, int right) {
//
//		int i = 0, j = 0, k = 0;
//		while (i < left && j < right) {
//			if (l[i].getRenderHeight() <= r[j].getRenderHeight()) {
//				a[k++] = l[i++];
//			} else {
//				a[k++] = r[j++];
//			}
//		}
//		while (i < left) {
//			a[k++] = l[i++];
//		}
//		while (j < right) {
//			a[k++] = r[j++];
//		}
//	}
	
}
