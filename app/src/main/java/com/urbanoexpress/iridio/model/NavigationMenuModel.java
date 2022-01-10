package com.urbanoexpress.iridio.model;

public class NavigationMenuModel {

    private Class<?> cls = null;
    private String title;
    private String description;
    private String badgeText;
    private int iconRes;
    private int typeAction;

    public interface TypeAction {
        int INTENT          = 1;
        int CLOSE_SESSION   = 2;
    }

    public NavigationMenuModel() { }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadgeText() {
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }

    public int getIcon() {
        return iconRes;
    }

    public void setIcon(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(int typeAction) {
        this.typeAction = typeAction;
    }

    public static class Builder {

        private Class<?> cls;
        private String title;
        private String description;
        private String badgeText;
        private int iconRes;
        private int typeAction;

        public Builder(int typeAction, Class<?> cls) {
            this.cls = cls;
            this.typeAction = typeAction;
        }

        public Builder(int typeAction) {
            this.cls = null;
            this.typeAction = typeAction;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setBadgeText(String badgeText) {
            this.badgeText = badgeText;
            return this;
        }

        public Builder setIcon(int iconRes) {
            this.iconRes = iconRes;
            return this;
        }

        public NavigationMenuModel build() {
            NavigationMenuModel menu = new NavigationMenuModel();
            menu.setCls(cls);
            menu.setTitle(title);
            menu.setDescription(description);
            menu.setBadgeText(badgeText);
            menu.setIcon(iconRes);
            menu.setTypeAction(typeAction);
            return menu;
        }
    }
}
