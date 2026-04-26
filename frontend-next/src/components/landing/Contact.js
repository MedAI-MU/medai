import { Mail, MapPin, Phone } from "lucide-react";
import Container from "../ui/Container";
import FormInput from "../ui/FormInput";
import TextArea from "../ui/TextArea";
import Section from "./Section";
import SectionTitle from "./SectionTitle";
import Button from "../ui/Button";
import ContactInfo from "./ContactInfo";

function Contact() {
  return (
    <Section id="contact">
      <Container>
        <SectionTitle
          title="Get In Touch"
          subTitle="Have questions? We'd love to hear from you."
        />

        <div className="grid gap-8 md:grid-cols-3">
          {/* Contact Form */}
          <div className="md:col-span-2">
            <div className="shadow-primary border-border-gray rounded-xl border bg-white p-8">
              <form className="space-y-6">
                <FormInput
                  label="Name"
                  type="text"
                  name="name"
                  placeholder="Your name"
                />
                <FormInput
                  label="Email"
                  type="email"
                  name="email"
                  placeholder="your.email@example.com"
                />
                <TextArea
                  label="Message"
                  rows={5}
                  placeholder="How can we help you?"
                />

                <Button type="submit" className="w-full">
                  Send Message
                </Button>
              </form>
            </div>
          </div>

          {/* Contact Info */}
          <div className="space-y-6">
            <div className="border-border-gray rounded-xl border bg-white p-6 shadow-[0_2px_4px_rgba(0,0,0,0.1)]">
              <ContactInfo
                icon={<Phone size={24} className="text-primary-blue" />}
                label="Phone"
                value="+1 (555) 123-4567"
              />
              <ContactInfo
                icon={<Mail size={24} className="text-primary-blue" />}
                label="Email"
                value="contact@medai.com"
              />
              <ContactInfo
                icon={<MapPin size={24} className="text-primary-blue" />}
                label="Location"
                value="123 Healthcare Ave, Medical District, NY 10001"
              />
            </div>
          </div>
        </div>
      </Container>
    </Section>
  );
}

export default Contact;
