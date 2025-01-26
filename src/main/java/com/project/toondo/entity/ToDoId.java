package com.project.toondo.entity;

import java.io.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ToDoId implements Serializable {

    private Long todoId; // 투두 ID
    private Long user;   // 사용자 ID
}
