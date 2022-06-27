package com.atqie.reggie.service.impl;

import com.atqie.reggie.entity.AddressBook;
import com.atqie.reggie.mapper.AddressBookMapper;
import com.atqie.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author 郄
 * @Date 2022/6/24 15:16
 * @Description:
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper,AddressBook> implements AddressBookService {
}
