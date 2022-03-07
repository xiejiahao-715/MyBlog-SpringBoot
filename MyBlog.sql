create database if not exists my_blog;

create table if not exists admin
(
    uid      int auto_increment
        primary key,
    username varchar(32) not null,
    password varchar(32) not null,
    constraint admin_username_uindex
        unique (username)
)
    comment '管理员表';

create table if not exists banner_image
(
    id  int auto_increment
        primary key,
    src text not null
)
    comment '存储banner图片';

create table if not exists blog
(
    id            int auto_increment
        primary key,
    blog_id       char(32)          not null comment '阿里云中博客文件的UUID',
    title         varchar(50)       not null comment '文章标题',
    cover         text              null comment '文章封面的路径',
    category      int               null comment '分类的ID',
    publish_time  datetime          null comment '发布时间',
    summary       text              null comment '文章内容的概括',
    is_top        tinyint default 0 not null comment '是否被置顶',
    is_hot        tinyint default 0 not null comment '是否为热门文章',
    view_count    bigint  default 0 not null comment '浏览数量
',
    comment_count bigint  default 0 not null comment '评论数',
    status        varchar(10)       null,
    constraint blog_blogId_uindex
        unique (blog_id)
)
    comment '所有博客';

create table if not exists blog_category
(
    id    int auto_increment
        primary key,
    title varchar(20) not null comment '分类名称',
    href  text        null comment '网站对应的路由'
)
    comment '博客的分类';

create table if not exists social
(
    id    int auto_increment
        primary key,
    title varchar(20) not null comment '社交方式',
    icon  varchar(20) not null comment '图标',
    color varchar(10) not null comment '图标颜色',
    href  text        not null comment '链接'
)
    comment '社交信息';

create table if not exists website_info
(
    id           int auto_increment
        primary key,
    name         varchar(50) not null comment '作者名称',
    avatar       text        null comment '头像链接(OOS相对路径)',
    domain       text        not null comment '域名',
    slogan       tinytext    null comment '个性签名',
    notice       tinytext    null comment '网站通知',
    `desc`       text        null comment '介绍',
    created_time datetime    null
)
    comment '网站信息';


