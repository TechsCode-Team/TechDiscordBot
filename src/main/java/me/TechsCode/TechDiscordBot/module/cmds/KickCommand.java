package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.List;

public class KickCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Staff"); }
    };

    public KickCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Kick a member from this guild.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "member", "The member to kick.", true),
                new OptionData(OptionType.STRING, "reason", "The reason to kick the member.", false)
        };
    }

    @Override
    public int getCooldown() {
        return 4;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        Member member = e.getOption("member").getAsMember();
        String reason = e.getOption("reason").getAsString();

        if (member.getRoles().contains(STAFF_ROLE.query().first())) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Kick - Error")
                            .error()
                            .text("You cannot kick this user")
                            .build()
            ).queue();
        } else if (member == e.getMember()) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Kick - Error")
                            .error()
                            .text("You cannot kick yourself")
                            .build()
            ).queue();
        } else {
            member.kick(reason).queue();

            e.replyEmbeds(
                    new TechEmbedBuilder("Kicked " + member.getUser().getName() + "#" + member.getUser().getDiscriminator())
                            .success()
                            .text("Successfully kicked " + member.getAsMention() + (reason == null ? "!" : " for `" + reason + "`!"))
                            .build()
            ).queue();
        }
    }
}
