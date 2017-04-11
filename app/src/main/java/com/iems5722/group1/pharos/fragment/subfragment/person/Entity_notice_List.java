package com.iems5722.group1.pharos.fragment.subfragment.person;

/**
 * Created by Sora on 10/4/17.
 */

public class Entity_Notice_List {
        public int action;
        public String content;
        public int readStatus;
        public int handleStatus;
        public Entity_Notice_List(int action, String content, int readStatus, int handleStatus) {
            super();
            this.action = action;
            this.content = content;
            this.readStatus = readStatus;
            this.handleStatus = handleStatus;
        }
        public void setAction(int action) {
            this.action = action;
        }
        public int getAction(){
            return action;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public void setReadStatus(int readStatus){this.readStatus = readStatus;}
        public int getReadStatus(){return readStatus;}
        public void setHandleStatus(int handleStatus){this.handleStatus = handleStatus;}
        public int getHandleStatus(){return this.handleStatus;}
        public Entity_Notice_List() {
            super();
            // TODO Auto-generated constructor stub
        }
}
