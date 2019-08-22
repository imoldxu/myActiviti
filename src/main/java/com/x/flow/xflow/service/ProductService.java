package com.x.flow.xflow.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

	public void hello() {
		List list = new ArrayList();
		Comparator c = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return 0;
			}
			
		};
		list.sort(c);
	}
	
}
