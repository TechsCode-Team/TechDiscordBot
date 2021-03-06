package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.awt.*;

public class SubVerifyCommand extends CommandModule {

    private final DefinedQuery<Role> SUB_VERIFIED = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Sub Verified");
        }
    };


    public SubVerifyCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "subverify";
    }

    @Override
    public String getDescription() {
        return "Add a Sub Verification which will be linked to your account.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }


    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "action", "Add or Remove a member as your sub verified user", true)
                        .addChoice("Add", "add")
                        .addChoice("Remove", "remove"),
                new OptionData(OptionType.USER, "member", "The member to sub verify which is then linked with your verification", true)
        };
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String action = e.getOption("action").getAsString();
        Member member = e.getOption("member").getAsMember();

        if(!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Sub Verification - Usage")
                            .error()
                            .text("Please use '**add**' or '**remove**' as action argument \n **Usage:** /SubVerify <Add | Remove> <Member>")
                            .build()
            ).queue();
            return;
        }

        if(!m.getRoles().contains(TechDiscordBot.getGuild().getRoleById(416174015141642240l))) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Sub Verification - Error")
                            .error()
                            .text("This command is only for **verified** user.")
                            .build()
            ).queue();
            return;
        }

        if(m.equals(member)) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Sub Verification - Error")
                    .error()
                    .text("You can't be your own sub verified user!")
                    .build()
            ).queue();
            return;
        }

        if(member.getRoles().contains(TechDiscordBot.getGuild().getRoleById(311188630922330112l))) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Sub Verification - Error")
                    .error()
                    .text("You can't a " + TechDiscordBot.getGuild().getRoleById(311188630922330112l).getAsMention() +  " as " + SUB_VERIFIED.query().first().getAsMention() + " users!")
                    .build()
            ).queue();
            return;
        }

        if(TechDiscordBot.getStorage().isSubVerifiedUser(member.getId()) && !TechDiscordBot.getStorage().getVerifiedIdFromSubVerifiedId(member.getId()).equals(m.getId())) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Sub Verification - Error")
                    .error()
                    .text("This user is already a " + SUB_VERIFIED.query().first().getAsMention() + " user of " + TechDiscordBot.getGuild().getMemberById(TechDiscordBot.getStorage().getVerifiedIdFromSubVerifiedId(member.getId())).getAsMention())
                    .build()
            ).queue();
            return;
        }

        if(action.equalsIgnoreCase("add")) {
            if(TechDiscordBot.getStorage().hasSubVerification(m.getId())) {
                e.replyEmbeds(
                        new TechEmbedBuilder("Sub Verification - Error")
                        .error()
                        .text("You need to remove your old sub verification first before you can add a new one.")
                        .build()
                ).queue();
                return;
            }

            TechDiscordBot.getGuild().addRoleToMember(member, SUB_VERIFIED.query().first()).queue();
            TechDiscordBot.getStorage().addSubVerification(m.getId(), member.getId());
            e.replyEmbeds(
                    new TechEmbedBuilder("Sub Verification - Added")
                            .success()
                            .text("Successfully **added** " + member.getAsMention() + " as " + m.getAsMention() + "'s sub verified user")
                            .build()
            ).queue();
            return;
        }

        if(action.equalsIgnoreCase("remove")) {
            if(TechDiscordBot.getStorage().getSubVerifiedIdFromVerifiedId(m.getId()) == null) {
             e.replyEmbeds(
                     new TechEmbedBuilder("Sub Verification - Error")
                     .error()
                     .text("You don't have an sub verified user!")
                     .build()
             ).queue();
             return;
            }

            if(TechDiscordBot.getStorage().getSubVerifiedIdFromVerifiedId(m.getId()) != null && !TechDiscordBot.getStorage().getSubVerifiedIdFromVerifiedId(m.getId()).equals(member.getId())) {
                e.replyEmbeds(
                        new TechEmbedBuilder("Sub Verification - Error")
                        .error()
                        .text(member.getAsMention() + " isn't your verified user!")
                        .build()
                ).queue();
                return;
            }

            TechDiscordBot.getGuild().removeRoleFromMember(member, SUB_VERIFIED.query().first()).queue();
            TechDiscordBot.getStorage().removeSubVerification(m.getId());
            e.replyEmbeds(
                    new TechEmbedBuilder("Sub Verification - Removed")
                            .color(Color.orange)
                            .text("Successfully **removed** " + member.getAsMention() + " as " + m.getAsMention() + "'s sub verified user")
                            .build()
            ).queue();
            return;
        }
    }
}
