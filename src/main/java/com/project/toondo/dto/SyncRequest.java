package com.project.toondo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {
    private List<ToDoRequest> toDoRequests;
    private List<Long> deletedTodoIds;

}
